package com.carpoor.alchabackend.service;


import com.carpoor.alchabackend.dto.AlertDto;
import com.carpoor.alchabackend.dto.AlertLevel;
import com.carpoor.alchabackend.dto.AlertType;
import com.carpoor.alchabackend.message.*;
import com.carpoor.alchabackend.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertCacheService {

    private final SseService sseService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 특정 차량의 모든 알림 조회
     */
    public List<AlertDto> getAll(String vehicleId) {
        String key = "vehicle:" + vehicleId + ":alerts";
        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
        if (objects == null) return List.of();
        return objects.stream()
                .filter(o -> o instanceof AlertDto)
                .map(o -> (AlertDto) o)
                .toList();
    }

    /**
     * 경사로 주차 감지
     */
    void checkRampParking(EventEngineStatusMessage eventMessage) {

        if (!("N".equalsIgnoreCase(eventMessage.getGearPositionMode()) &&
                "OFF".equalsIgnoreCase(eventMessage.getSideBrakeStatus())))
            return;

        if (eventMessage.getInclinationSensor() < 2.0)
            return;

        String message = "경사로에 주차중입니다. 차가 밀릴 위험이 있습니다.";

        AlertDto alertDto = new AlertDto(
                eventMessage.getVehicleId(),
                AlertType.RAMP_PARKING,
                AlertLevel.CAUTION,
                message,
                eventMessage.getTimestamp()
        );

        sseService.sendAlert(alertDto);
        saveAlert(alertDto);

        log.info("ramp parking alert 생성 성공: dto={}", alertDto);
    }

    /**
     * 차량 내부 온도 경고
     */
    @Async
    void checkHighTemperature(String vehicleId, double curTemperature, String timestamp) {

        if (curTemperature < 40) return;

        // 주차 상태 확인
        String key = "vehicle:" + vehicleId + ":realtime";
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object value = ops.get(key);
        if (!(value instanceof RealtimeAppDataMessage realtime)) return;
        if ("ON".equalsIgnoreCase(realtime.getEngineStatusIgnition())) return;

        // 최근 알림 확인
        AlertDto alert = getLatestHighTemperatureAlert(vehicleId);
        if (alert != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime alertTime = LocalDateTime.parse(alert.getTimestamp(), formatter);
            LocalDateTime now = LocalDateTime.now();

            Duration diff = Duration.between(alertTime, now);
            if (diff.toMinutes() < 30) return;
        }

        AlertDto alertDto = new AlertDto(
                vehicleId,
                AlertType.HIGH_TEMPERATURE,
                AlertLevel.WARNING,
                "차량 내부 온도가 " + curTemperature + "°C 입니다. 전자기기, 위험물 등이 있는지 확인하세요.\n",
                timestamp
        );

        saveAlert(alertDto);
        sseService.sendAlert(alertDto);
        log.info("high temperature alert 생성 성공: dto={}", alertDto);
    }

    /**
     * 최근 HIGH_TEMPERATURE 알림 가져오기
     */
    public AlertDto getLatestHighTemperatureAlert(String vehicleId) {
        String key = "vehicle:" + vehicleId + ":alerts";
        List<Object> recentObjects = redisTemplate.opsForList().range(key, 0, 50);
        if (recentObjects == null || recentObjects.isEmpty()) return null;

        for (Object obj : recentObjects) {
            if (obj instanceof AlertDto alert &&
                    alert.getAlertType() == AlertType.HIGH_TEMPERATURE) {
                return alert;
            }
        }
        return null;
    }

    /**
     * 급발진 경고
     */
    void checkSuddenUnintendedAcceleration(EventSuddenAccelerationMessage eventMessage) {
        if (eventMessage.getThrottlePosition() > 5) return;

        AlertDto alertDto = new AlertDto(
                eventMessage.getVehicleId(),
                AlertType.SUDDEN_UNINTENDED_ACCELERATION,
                AlertLevel.DANGER,
                "급발진이 의심됩니다.",
                eventMessage.getTimestamp()
        );

        saveAlert(alertDto);
        sseService.sendAlert(alertDto);
        log.info("sudden unintended acceleration alert 생성 성공: dto={}", alertDto);
    }

    /**
     * 경고등 알림
     */
    void sendWarningLightAlert(EventWarningLightMessage eventMessage) {
        String type = eventMessage.getType();
        AlertType alertType;
        String message;

        switch (type) {
            case "engine_oil_check" -> {
                alertType = AlertType.ENGINE_OIL_CHECK;
                message = "엔진오일 점검 경고등이 켜졌습니다. 엔진오일량과 누유 여부를 확인하세요.";
            }
            case "engine_check" -> {
                alertType = AlertType.ENGINE_CHECK;
                message = "엔진 점검 경고등이 켜졌습니다. 엔진 이상 여부를 진단받으세요.";
            }
            case "airbag_check" -> {
                alertType = AlertType.AIRBAG_CHECK;
                message = "에어백 경고등이 켜졌습니다. 안전장치의 오작동 가능성이 있습니다. 정비소 방문을 권장합니다.";
            }
            case "coolant_check" -> {
                alertType = AlertType.COOLANT_CHECK;
                message = "냉각수 경고등이 켜졌습니다. 냉각수 부족 또는 과열을 점검하세요.";
            }
            default -> throw new IllegalArgumentException("Unknown warning light type: " + type);
        }

        AlertDto alertDto = new AlertDto(
                eventMessage.getVehicleId(),
                alertType,
                AlertLevel.CAUTION,
                message,
                eventMessage.getTimestamp()
        );

        saveAlert(alertDto);
        sseService.sendAlert(alertDto);
        log.info("warning light alert 생성 성공: dto={}", alertDto);
    }

    /**
     * 충돌 경고
     */
    void sendCollisionAlert(EventCollisionMessage eventMessage) {
        double damage = eventMessage.getDamage();
        AlertLevel alertLevel;
        String message;

        if (damage < 60) {
            alertLevel = AlertLevel.CAUTION;
            message = "경미한 접촉이 감지되었습니다. 차량 외관 점검을 권장합니다.";
        } else if (damage < 95) {
            alertLevel = AlertLevel.WARNING;
            message = "심한 충돌이 감지되었습니다. 차량 상태를 점검하세요.";
        } else {
            alertLevel = AlertLevel.DANGER;
            message = "전손 수준의 충돌이 감지되었습니다.";
        }

        AlertDto alertDto = new AlertDto(
                eventMessage.getVehicleId(),
                AlertType.COLLISION,
                alertLevel,
                message,
                eventMessage.getTimestamp()
        );

        saveAlert(alertDto);
        sseService.sendAlert(alertDto);
        log.info("collision alert 생성 성공: dto={}", alertDto);
    }

    /**
     * Redis에 알림 저장
     */
    void saveAlert(AlertDto alertDto) {
        redisTemplate.opsForList().leftPush("vehicle:" + alertDto.getVehicleId() + ":alerts", alertDto);
    }
}
