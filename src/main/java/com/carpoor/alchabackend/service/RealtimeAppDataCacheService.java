package com.carpoor.alchabackend.service;

import com.carpoor.alchabackend.message.RealtimeAppDataMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealtimeAppDataCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRealtimeAppData(RealtimeAppDataMessage message) {
        String key = "vehicle:" + message.getVehicleId() + ":realtime";
        redisTemplate.opsForValue().set(key, message);
    }

    public RealtimeAppDataMessage getRealtimeAppData(String vehicleId) {
        String key = "vehicle:" + vehicleId + ":realtime";
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof RealtimeAppDataMessage) {
            return (RealtimeAppDataMessage) value;
        } else {
            return null;
        }
    }
}
