package com.carpoor.alchabackend.service;

import com.carpoor.alchabackend.message.EventCollisionMessage;
import com.carpoor.alchabackend.message.EventEngineStatusMessage;
import com.carpoor.alchabackend.message.EventSuddenAccelerationMessage;
import com.carpoor.alchabackend.message.EventWarningLightMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumerService {

    private final AlertCacheService alertCacheService;

    @KafkaListener(
            topics = "event-engine-status",
            groupId = "event-status-group"
    )
    public void consumeEventEngineStatusMessage(EventEngineStatusMessage eventMessage) {
        if (eventMessage.getEngineStatusIgnition().equals("OFF")) alertCacheService.checkRampParking(eventMessage);
    }

    @KafkaListener(
            topics = "event-sudden-acceleration",
            groupId = "event-alert-group"
    )
    public void consumeEventSuddenAccelerationMessage(EventSuddenAccelerationMessage eventMessage) {
        alertCacheService.checkSuddenUnintendedAcceleration(eventMessage);
    }

    @KafkaListener(
            topics = "event-collision",
            groupId = "event-alert-group"
    )
    public void consumeEventCollisionMessage(EventCollisionMessage eventMessage) {
        alertCacheService.sendCollisionAlert(eventMessage);
    }

    @KafkaListener(
            topics = "event-warning-light",
            groupId = "event-alert-group"
    )
    public void consumeEventWarningLightMessage(EventWarningLightMessage eventMessage) {
        alertCacheService.sendWarningLightAlert(eventMessage);
    }

}
