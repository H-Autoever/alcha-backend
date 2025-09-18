package com.carpoor.alchabackend.sse;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.MediaType;

/**
 * SSE (Server-Sent Events) 컨트롤러
 * front와 실시간 데이터 통신 
 */
@RestController
@CrossOrigin(origins = "*")
public class SseController {
    
    // front와 연결할 엔드포인트
    @GetMapping(value = "/api/sse/{vehicleId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectSse(@PathVariable String vehicleId) {
        // SSE 연결 생성 (30분 타임아웃)
        SseEmitter emitter = new SseEmitter(1800000L);
        
        // 연결 완료 시 로그 출력
        emitter.onCompletion(() -> System.out.println("SSE 연결 완료: " + vehicleId));
        
        // 연결 에러 시 로그 출력
        emitter.onError((throwable) -> System.out.println("SSE 연결 에러: " + vehicleId + ", " + throwable.getMessage()));
        
        // 연결 타임아웃 시 로그 출력
        emitter.onTimeout(() -> System.out.println("SSE 연결 타임아웃: " + vehicleId));
        
        // 연결 즉시 테스트 데이터 전송
        try {
            emitter.send(SseEmitter.event()
                .name("test")
                .data("SSE 연결 성공! 차량 ID: " + vehicleId));
        } catch (Exception e) {
            System.out.println("테스트 데이터 전송 실패: " + e.getMessage());
        }
        
        return emitter;
    }
}

