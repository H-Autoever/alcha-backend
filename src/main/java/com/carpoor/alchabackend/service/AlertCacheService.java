package com.carpoor.alchabackend.service;


import com.carpoor.alchabackend.dto.AlertDto;
import com.carpoor.alchabackend.dto.AlertType;
import com.carpoor.alchabackend.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertCacheService {
    private final SseService sseService;
    private final RedisTemplate<String, Object> redisTemplate;


    void checkRampParking(String vehicleId, String timestamp) {
        // 엔진 off 이벤트, 사이드브레이크 없고, 중립기어 일 때, 경사로면 알림
        AlertDto alertDto = AlertDto.builder()
                .vehicleId(vehicleId)
                .alertType(AlertType.RAMP_PARKING)
                .message("경사로에 주차중입니다.")
                .timestamp(timestamp)
                .build();
        sseService.sendAlert(alertDto.getVehicleId(), alertDto);

        String key = "vehicle:" + vehicleId + ":alert:ramp-parking";
        redisTemplate.opsForValue().set(key, alertDto);

        log.info("ramp parking alert 생성 성공: vehicleId={}, dto={}", vehicleId, alertDto);

    }

    void checkHighTemperature(String vehicleId, String timestamp) {
        //특정 온도 이상 올라가면, 주차 상태 확인, 알람 보냈는지 확인 후 전송
        //그 후 kafka 토픽으로도 전송
//        AlertDto alertDto = AlertDto.builder()
//                .vehicleId("CAR-001")
//                .alertType(AlertType.HIGH_TEMPERATURE)
//                .message("엔진이 과열되었습니다.")
//                .timestamp("2025-10-20T10:15:00Z")
//                .build();
//        sseService.sendAlert(alertDto.getVehicleId(), alertDto);
    }

    void checkSuddenUnintendedAcceleration(String vehicleId, String timestamp) {
        // 가속 페달 누른 거에 비해 속도 빨리올라가면?
        // 아니면 브레이크 밟는데 속도가 올라가면?
        AlertDto alertDto = AlertDto.builder()
                .vehicleId(vehicleId)
                .alertType(AlertType.SUDDEN_UNINTENDED_ACCELERATION)
                .message("급발진이 의심됩니다.")
                .timestamp(timestamp)
                .build();
        sseService.sendAlert(alertDto.getVehicleId(), alertDto);

        String key = "vehicle:" + vehicleId + ":alert:sudden-unintended-acceleration";
        redisTemplate.opsForValue().set(key, alertDto);

        log.info("sudden unintended acceleration alert 생성 성공: vehicleId={}, dto={}", vehicleId, alertDto);

    }

    void checkCollision(String vehicleId, String timestamp) {
        AlertDto alertDto = AlertDto.builder()
                .vehicleId(vehicleId)
                .alertType(AlertType.COLLISION)
                .message("충돌이 발생했습니다.")
                .timestamp(timestamp)
                .build();
        sseService.sendAlert(alertDto.getVehicleId(), alertDto);

        String key = "vehicle:" + vehicleId + ":alert:collision";
        redisTemplate.opsForValue().set(key, alertDto);

        log.info("collision alert 생성 성공: vehicleId={}, dto={}", vehicleId, alertDto);
    }


    public List<AlertDto> getAll(String vehicleId) {
        String pattern = "vehicle:" + vehicleId + ":alert*";
        List<AlertDto> alerts = new ArrayList<>();

        // SCAN을 이용해 패턴에 맞는 키 검색
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions().match(pattern).count(100).build());

        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            Object value = redisTemplate.opsForValue().get(key);

            if (value instanceof AlertDto alertDto) {
                alerts.add(alertDto);
            }
        }
        try {
            cursor.close();
        } catch (Exception ignored) {
        }
        return alerts;
    }
}
