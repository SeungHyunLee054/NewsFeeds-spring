package com.nbc.newsfeeds.common.redis.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RedisHash(value = "BlackList")
public class AccessTokenBlackList {
	@Id
	private String accessToken;

	@TimeToLive
	private Long expiration;
}
