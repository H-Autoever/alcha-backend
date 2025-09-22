package com.carpoor.alchabackend.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE (Server-Sent Events) 컨트롤러
 * front와 실시간 데이터 통신
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SseController {

    private final SseService sseService;

    // front와 연결할 엔드 포인트
    @GetMapping(value = "/api/sse/{vehicleId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectSse(@PathVariable String vehicleId) {

//        // 연결 즉시 테스트 데이터 전송
//        try {
//            emitter.send(SseEmitter.event()
//                // name 없는 기본 이벤트로 설정하려고 이벤트 타입 제거
//                .data("SSE 연결 성공! 차량 ID: " + vehicleId));
//        } catch (Exception e) {
//            System.out.println("테스트 데이터 전송 실패: " + e.getMessage());
//        }

        return sseService.createEmitter(vehicleId);
    }
}
