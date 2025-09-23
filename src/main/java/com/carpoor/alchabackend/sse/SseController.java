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
        return sseService.createEmitter(vehicleId);
    }
}
