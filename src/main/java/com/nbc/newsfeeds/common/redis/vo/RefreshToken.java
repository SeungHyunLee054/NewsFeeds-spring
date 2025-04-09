package com.nbc.newsfeeds.common.redis.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RedisHash(value = "RefreshToken")
public class RefreshToken {
	@Id
	private String email;

	private String refreshToken;

	@TimeToLive
	private Long expiration;
}
