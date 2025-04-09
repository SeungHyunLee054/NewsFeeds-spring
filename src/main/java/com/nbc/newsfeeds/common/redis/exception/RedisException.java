package com.nbc.newsfeeds.common.redis.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;
import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;

@Getter
public class RedisException extends BaseException {
	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public RedisException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
