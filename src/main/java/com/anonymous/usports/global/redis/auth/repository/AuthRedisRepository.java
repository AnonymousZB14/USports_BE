package com.anonymous.usports.global.redis.auth.repository;

public interface AuthRedisRepository {
    void saveEmailAuthNumber(String email, String number);

    int getEmailAuthNumber(String email);
}
