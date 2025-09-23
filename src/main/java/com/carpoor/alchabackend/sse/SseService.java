package com.carpoor.alchabackend.sse;

import com.carpoor.alchabackend.dto.PeriodicAppDataDto;
import com.carpoor.alchabackend.dto.RealtimeAppDataDto;
import com.carpoor.alchabackend.message.PeriodicAppDataMessage;
import com.carpoor.alchabackend.message.RealtimeAppDataMessage;
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

        log.info("SSE emitter 생성={}", emitter);

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

        // 연결 즉시 테스트 데이터 전송
        try {
            emitter.send(SseEmitter.event().data("SSE 연결 성공! 차량 ID: " + vehicleId));
        } catch (Exception e) {
            System.out.println("테스트 데이터 전송 실패: " + e.getMessage());
        }

        return emitter;
    }

    private void removeEmitter(String vehicleId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(vehicleId);
        if (list != null) {
            list.remove(emitter);
        }
    }

    public void sendRealtimeData(String vehicleId, RealtimeAppDataMessage message) {
        RealtimeAppDataDto dto = new RealtimeAppDataDto(message);
        List<SseEmitter> list = emitters.get(vehicleId);
        if (list != null) {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().name("realtime_data").data(dto));
                    log.info("realtime_data 전송 성공: vehicleId={}, dto={}", vehicleId, dto.toString());

                } catch (Exception e) {
                    log.error("realtime_data 전송 실패: vehicleId={}, error={}", vehicleId, e.getMessage());
                    removeEmitter(vehicleId, emitter);
                }
            }
        }
    }

    public void sendPeriodicData(String vehicleId, PeriodicAppDataMessage message) {
        PeriodicAppDataDto dto = new PeriodicAppDataDto(message);
        List<SseEmitter> list = emitters.get(vehicleId);
        if (list != null) {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().name("periodic_data").data(dto));
                    log.info("periodic_data 전송 성공: vehicleId={}, dto={}", vehicleId, dto);
                } catch (Exception e) {
                    log.error("periodic_data 전송 실패: vehicleId={}, error={}", vehicleId, e.getMessage());
                    removeEmitter(vehicleId, emitter);
                }
            }
        }
    }


}
