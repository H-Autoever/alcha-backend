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

    @KafkaListener(
            topics = "realtime-app-data",
            groupId = "realtime-group",
            containerFactory = "realtimeAppDataListenerContainerFactory"
    )
    public void consumeRealtimeMessage(RealtimeAppDataMessage message) {
        log.info("Received Realtime message: {}", message);
    }

    @KafkaListener(
            topics = "periodic-app-data",
            groupId = "periodic-group",
            containerFactory = "periodicAppDataListenerContainerFactory"
    )
    public void consumePeriodic(PeriodicAppDataMessage message) {
        log.info("Received Periodic message: {}", message);
    }
}
