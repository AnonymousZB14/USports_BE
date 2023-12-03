package com.anonymous.usports.global.redis.auth.repository;

import com.anonymous.usports.global.constant.MailConstant;
import com.anonymous.usports.global.exception.ErrorCode;
import com.anonymous.usports.global.exception.MemberException;
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
        values.set(MailConstant.PREFIX_AUTH + email,
                number,
                Duration.ofMinutes(MailConstant.AUTH_EMAIL_VALID_TIME));
    }

    @Override
    public int getEmailAuthNumber(String email) {
        String emailKey = MailConstant.PREFIX_AUTH + email;

        if (!redisTemplate.hasKey(emailKey)) {
            throw new MemberException(ErrorCode.EMAIL_AUTH_NUMBER_EXPIRED);
        }

        ValueOperations<String, String> values = redisTemplate.opsForValue();

        return Integer.parseInt(values.get(emailKey));
    }

}
