package com.t1tanic.driverauth.vehiclevalidation.service;


import org.springframework.data.redis.core.RedisTemplate;

public interface TokenBlacklistService {
    boolean isTokenBlacklisted(String token);
    void blacklistToken(String token, long expirationInSeconds);
}
