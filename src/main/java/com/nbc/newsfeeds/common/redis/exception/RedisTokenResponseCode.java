package com.nbc.newsfeeds.common.redis.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.model.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisTokenResponseCode implements ResponseCode {
	NOT_FOUND(false, HttpStatus.NOT_FOUND, "refresh token을 찾을 수 없습니다.");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}
