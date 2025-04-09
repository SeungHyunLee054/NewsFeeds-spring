package com.nbc.newsfeeds.common.jwt.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtTokenExceptionCode implements ResponseCode {
	REFRESH_TOKEN_EXPIRED(false, HttpStatus.FORBIDDEN, "refresh token이 만료되었습니다."),
	NOT_MATCHES_REFRESH_TOKEN(false, HttpStatus.FORBIDDEN, "유저의 refresh token이 아닙니다."),
	NOT_VALID_JWT_TOKEN(false, HttpStatus.FORBIDDEN, "옳바르지 않은 JWT 토큰입니다."),
	NOT_VALID_SIGNATURE(false, HttpStatus.FORBIDDEN, "서명이 옳바르지 않습니다."),
	NOT_VALID_CONTENT(false, HttpStatus.FORBIDDEN, "내용이 옳바르지 않습니다.");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}
