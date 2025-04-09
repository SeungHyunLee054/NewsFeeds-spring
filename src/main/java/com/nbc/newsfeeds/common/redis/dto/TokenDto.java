package com.nbc.newsfeeds.common.redis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
	private String email;

	private String token;

	private Long timeToLive;
}
