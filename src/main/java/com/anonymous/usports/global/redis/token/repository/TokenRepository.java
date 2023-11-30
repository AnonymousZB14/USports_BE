package com.anonymous.usports.global.redis.token.repository;

public interface TokenRepository {

    public void saveToken(String refreshToken, String email);

    public String getToken(String email);

    public boolean deleteToken(String email);

    public void addBlackListAccessToken(String accessToken);

    public boolean existsBlackListAccessToken(String accessToken);
}
