package com.nbc.newsfeeds.common.jwt.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;

import lombok.Getter;

@Getter
public class JwtTokenException extends BaseException {
	private final JwtTokenExceptionCode responseCode;
	private final HttpStatus httpStatus;
	private final String message;

	public JwtTokenException(JwtTokenExceptionCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
		this.message = responseCode.getMessage();
	}
}
