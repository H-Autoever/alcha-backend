package com.carpoor.alchabackend.service;

import com.carpoor.alchabackend.message.PeriodicAppDataMessage;
import com.carpoor.alchabackend.message.RealtimeAppDataMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppDataConsumerService {

    private final RealtimeAppDataCacheService realtimeAppDataCacheService;
    private final PeriodicAppDataCacheService periodicAppDataCacheService;

    @KafkaListener(
            topics = "realtime-app-data",
            groupId = "realtime-group",
            containerFactory = "realtimeAppDataListenerContainerFactory"
    )
    public void consumeRealtimeMessage(RealtimeAppDataMessage message) {
        realtimeAppDataCacheService.saveRealtimeAppData(message);

        /* TODO: SSE 연결로 데이터 전송 */

        /* TODO: 시나리오 검사 후 알림 전송 */
    }

    @KafkaListener(
            topics = "periodic-app-data",
            groupId = "periodic-group",
            containerFactory = "periodicAppDataListenerContainerFactory"
    )
    public void consumePeriodic(PeriodicAppDataMessage message) {
        periodicAppDataCacheService.savePeriodicAppData(message);

        /* TODO: SSE 연결로 데이터 전송 */

        /* TODO: 시나리오 검사 후 알림 전송 */
    }
}
