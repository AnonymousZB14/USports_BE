package com.anonymous.usports.global.redis.token.repository;

import com.anonymous.usports.global.constant.TokenConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepository implements TokenRepository {

    private final RedisTemplate redisTemplate;

    @Override
    public void saveToken(String refreshToken, String email) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(TokenConstant.REFRESH_TOKEN_PREFIX + email,
                refreshToken,
                Duration.ofMillis(TokenConstant.REFRESH_TOKEN_VALID_TIME));
    }

    @Override
    public String getToken(String email) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(TokenConstant.REFRESH_TOKEN_PREFIX + email);
    }

    // 로그아웃 할 때
    @Override
    public boolean deleteToken(String email) {
        boolean result = redisTemplate.hasKey(TokenConstant.REFRESH_TOKEN_PREFIX + email);

        if (result) redisTemplate.delete(TokenConstant.REFRESH_TOKEN_PREFIX + email);

        return result;
    }

    /**
     * 로그아웃 할 때에, accessToken 만료 시간이 남아 있을 수 있음으로, 레디스에 저장
     * @param accessToken
     */
    @Override
    public void addBlackListAccessToken(String accessToken) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(TokenConstant.ACCESS_TOKEN_PREFIX + accessToken,
                TokenConstant.BLACK_LIST,
                TokenConstant.ACCESS_TOKEN_VALID_TIME);
    }

    /**
     * 로그아웃 할 때에, accessToken 만료 시간이 남아 있을 수 있음으로, 레디스에 저장
     * 해당 accessToken은 사용하지 못 한다
     * @param accessToken
     */
    @Override
    public boolean existsBlackListAccessToken(String accessToken) {
        return redisTemplate.hasKey(TokenConstant.ACCESS_TOKEN_PREFIX + accessToken);
    }
}
