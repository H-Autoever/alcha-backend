package com.carpoor.alchabackend.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SseService {
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(String vehicleId) {
        SseEmitter emitter = new SseEmitter(1800000L);

        emitters.computeIfAbsent(vehicleId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        // 연결 해제 이벤트 처리
        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료: vehicleId={}", vehicleId);
            removeEmitter(vehicleId, emitter);
        });
        emitter.onTimeout(() -> {
            log.warn("SSE 연결 타임아웃: vehicleId={}", vehicleId);
            removeEmitter(vehicleId, emitter);
        });
        emitter.onError((e) -> {
            log.error("SSE 연결 에러: vehicleId={}, error={}", vehicleId, e.getMessage());
            removeEmitter(vehicleId, emitter);
        });

        return emitter;
    }

    private void removeEmitter(String vehicleId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(vehicleId);
        if (list != null) {
            list.remove(emitter);
        }
    }
}
