package com.carpoor.alchabackend.service;

import com.carpoor.alchabackend.message.PeriodicAppDataMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PeriodicAppDataCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final AlertCacheService alertCacheService;

    public void savePeriodicAppData(PeriodicAppDataMessage message) {
        String key = "vehicle:" + message.getVehicleId() + ":periodic";
        redisTemplate.opsForValue().set(key, message);

        alertCacheService.checkHighTemperature(message.getVehicleId(), message.getTemperatureCabin(), message.getTimestamp());
    }

    public PeriodicAppDataMessage getPeriodicAppData(String vehicleId) {
        String key = "vehicle:" + vehicleId + ":periodic";
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof PeriodicAppDataMessage) {
            return (PeriodicAppDataMessage) value;
        } else {
            return null;
        }
    }
}
