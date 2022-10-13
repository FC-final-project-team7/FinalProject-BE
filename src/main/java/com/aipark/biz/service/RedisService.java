package com.aipark.biz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, String> redisWhiteListTemplate;
    private final RedisTemplate<String, String> redisBlackListTemplate;

    public void setValues(String key, String data, Duration duration){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getValues(String key){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void deleteValues(String key){
        redisTemplate.delete(key);
    }

    public void setWhiteValues(String key, String data, Duration duration){
        ValueOperations<String, String> values = redisWhiteListTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getWhiteValues(String key){
        ValueOperations<String, String> values = redisWhiteListTemplate.opsForValue();
        return values.get(key);
    }

    public void deleteWhiteValues(String key){
        redisWhiteListTemplate.delete(key);
    }

    public void setBlackValues(String key, String data, Duration duration){
        ValueOperations<String, String> values = redisBlackListTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getBlackValues(String key){
        ValueOperations<String, String> values = redisBlackListTemplate.opsForValue();
        return values.get(key);
    }

    public void deleteBlackValues(String key){
        redisBlackListTemplate.delete(key);
    }
}
