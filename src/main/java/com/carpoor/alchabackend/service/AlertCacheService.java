package com.carpoor.alchabackend.service;


import com.carpoor.alchabackend.dto.AlertDto;
import com.carpoor.alchabackend.dto.AlertLevel;
import com.carpoor.alchabackend.dto.AlertType;
import com.carpoor.alchabackend.message.*;
import com.carpoor.alchabackend.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, AlertDto> redisTemplate;
    private final RedisTemplate<String, RealtimeAppDataMessage> realtimeTemplate;

    public List<AlertDto> getAll(String vehicleId) {
        String key = "vehicle:" + vehicleId + ":alerts";
        return redisTemplate.opsForList().range(key, 0, -1);

    }

    void checkRampParking(EventEngineStatusMessage eventMessage) {

        if (!("N".equalsIgnoreCase(eventMessage.getGearPositionMode()) && "OFF".equalsIgnoreCase(eventMessage.getSideBrakeStatus())))
            return;
        if (eventMessage.getInclinationSensor() < 2.0) return;


        String message = "경사로에 주차중입니다. 차가 밀릴 위험이 있습니다.";

        AlertDto alertDto = new AlertDto(eventMessage.getVehicleId(),
                AlertType.RAMP_PARKING,
                AlertLevel.CAUTION,
                message,
                eventMessage.getTimestamp());

        sseService.sendAlert(alertDto);

        saveAlert(alertDto);

        log.info("ramp parking alert 생성 성공: dto={}", alertDto);

    }

    @Async
    void checkHighTemperature(String vehicleId, double curTemperature, String timestamp) {

        if (curTemperature < 40) {
            return;
        }

        // 주차 상태 확인
        String key = "vehicle:" + vehicleId + ":realtime";
        RealtimeAppDataMessage value = realtimeTemplate.opsForValue().get(key);
        if (Objects.requireNonNull(value).getEngineStatusIgnition().equals("ON")) {
            return;
        }

        // 알림 보냈었는지 확인
        AlertDto alert = getLatestHighTemperatureAlert(vehicleId);
        if (alert == null) {
            return;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime alertTime = LocalDateTime.parse(alert.getTimestamp(), formatter);

            // 2️⃣ 현재 시각
            LocalDateTime now = LocalDateTime.now();

            // 3️⃣ 시간 차 계산
            Duration diff = Duration.between(alertTime, now);

            // 4️⃣ 30분(=1800초) 이내
            if (diff.toMinutes() < 30) return;
        }

        //알림 생성
        AlertDto alertDto = new AlertDto(vehicleId,
                AlertType.SUDDEN_UNINTENDED_ACCELERATION,
                AlertLevel.WARNING,
                "차량 내부 온도가 " + curTemperature + "°C 입니다. 전자기기, 위험물 등이 있는지 확인하세요.\n",
                timestamp);

        saveAlert(alertDto);

        sseService.sendAlert(alertDto);

        log.info("high temperature alert 생성 성공: dto={}", alertDto);
    }

    public AlertDto getLatestHighTemperatureAlert(String vehicleId) {
        String key = "vehicle:" + vehicleId + ":alerts";

        // 최근 몇 개만 우선 확인 (너무 크면 성능 저하 방지)
        List<AlertDto> recentAlerts = redisTemplate.opsForList().range(key, 0, 50);
        if (recentAlerts == null || recentAlerts.isEmpty()) {
            return null;
        }

        // 가장 최근부터 순회하면서 HIGH_TEMPERATURE 찾기
        for (AlertDto alert : recentAlerts) {
            if (alert.getAlertType() == AlertType.HIGH_TEMPERATURE) {
                return alert;  // 첫 번째로 발견된 것 = 가장 최근
            }
        }

        return null; // 없을 경우
    }


    void checkSuddenUnintendedAcceleration(EventSuddenAccelerationMessage eventMessage) {
        // TODO: 쓰로틀 0일 때 급발진 의심
        if (eventMessage.getThrottlePosition() > 5) return;

        AlertDto alertDto = new AlertDto(eventMessage.getVehicleId(),
                AlertType.SUDDEN_UNINTENDED_ACCELERATION,
                AlertLevel.DANGER,
                "급발진이 의심됩니다.",
                eventMessage.getTimestamp());

        saveAlert(alertDto);

        sseService.sendAlert(alertDto);

        log.info("sudden unintended acceleration alert 생성 성공: dto={}", alertDto);

    }

    void sendWarningLightAlert(EventWarningLightMessage eventMessage) {
        String type = eventMessage.getType();
        AlertType alertType;
        String message;

        // ----- type 값에 따른 AlertType 및 메시지 매핑 -----
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

    void saveAlert(AlertDto alertDto) {
        redisTemplate.opsForList().leftPush("vehicle:" + alertDto.getVehicleId() + ":alerts", alertDto);
    }
}
