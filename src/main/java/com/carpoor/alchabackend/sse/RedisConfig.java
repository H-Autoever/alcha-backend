package com.carpoor.alchabackend.sse;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Redis 설정 클래스
 * Redis 연결 및 템플릿 설정
 */
@Configuration
public class RedisConfig {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // TODO: Redis 템플릿 설정 및 커스텀 설정 추가 
}
