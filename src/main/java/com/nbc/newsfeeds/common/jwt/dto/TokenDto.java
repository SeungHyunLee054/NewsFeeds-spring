package com.nbc.newsfeeds.common.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
	private final String accessToken;
	private final String refreshToken;
}
