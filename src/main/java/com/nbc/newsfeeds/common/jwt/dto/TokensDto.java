package com.nbc.newsfeeds.common.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokensDto {
	private final String accessToken;
	private final String refreshToken;
}
