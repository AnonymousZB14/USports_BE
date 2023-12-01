package com.anonymous.usports.global.redis.auth.repository;

import com.anonymous.usports.global.constant.EmailConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class EmailAuthRedisRepository implements AuthRedisRepository{

    private final RedisTemplate redisTemplate;

    @Override
    public void saveEmailAuthNumber(String email, String number) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(EmailConstant.PREFIX_AUTH + email, number, Duration.ofMinutes(EmailConstant.AUTH_EMAIL_VALID_TIME));
    }
}
