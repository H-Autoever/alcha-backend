package com.carpoor.alchabackend.service;


import com.carpoor.alchabackend.dto.AlertDto;
import com.carpoor.alchabackend.dto.AlertLevel;
import com.carpoor.alchabackend.dto.AlertType;
import com.carpoor.alchabackend.message.EventCollisionMessage;
import com.carpoor.alchabackend.message.EventEngineStatusMessage;
import com.carpoor.alchabackend.message.EventSuddenAccelerationMessage;
import com.carpoor.alchabackend.message.EventWarningLightMessage;
import com.carpoor.alchabackend.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertCacheService {
    private final SseService sseService;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<AlertDto> getAll(String vehicleId) {
        String key = "vehicle:" + vehicleId + ":alerts";
        List<Object> values = redisTemplate.opsForList().range(key, 0, -1);

        List<AlertDto> alerts = new ArrayList<>();
        if (values != null) {
            for (Object v : values) {
                if (v instanceof AlertDto alert) alerts.add(alert);
            }
        }
        return alerts;
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

    void checkHighTemperature(String vehicleId, String timestamp) {
        // TODO: 특정 온도 이상 올라가면, 주차 상태 확인, 알람 보냈었는지 확인 후 전송

//        AlertDto alertDto = AlertDto.builder()
//                .vehicleId("CAR-001")
//                .alertType(AlertType.HIGH_TEMPERATURE)
//                .message("엔진이 과열되었습니다.")
//                .timestamp("2025-10-20T10:15:00Z")
//                .build();
//        sseService.sendAlert(alertDto.getVehicleId(), alertDto);
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
