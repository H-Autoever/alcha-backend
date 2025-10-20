package com.carpoor.alchabackend.service;

import com.carpoor.alchabackend.message.EventMessage;
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
            topics = "event-engine-off",
            groupId = "event-group"
    )
    public void consumeEngineOffEventMessage(EventMessage message) {
        alertCacheService.checkRampParking(message.getVehicleId(), message.getTimestamp());
    }

    @KafkaListener(
            topics = "event-sudden-acceleration",
            groupId = "event-group"
    )
    public void consumeSuddenAccelerationEventMessage(EventMessage message) {
        alertCacheService.checkSuddenUnintendedAcceleration(message.getVehicleId(), message.getTimestamp());
    }

    @KafkaListener(
            topics = "event-collision",
            groupId = "event-group"
    )
    public void consumeCollisionEventMessage(EventMessage message) {
        alertCacheService.checkCollision(message.getVehicleId(), message.getTimestamp());
    }
}
