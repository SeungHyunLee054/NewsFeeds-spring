package com.nbc.newsfeeds.common.redis.repository;

import org.springframework.data.repository.CrudRepository;

import com.nbc.newsfeeds.common.redis.vo.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
