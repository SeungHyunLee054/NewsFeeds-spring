package com.nbc.newsfeeds.common.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

public abstract class BaseException extends RuntimeException {
	public abstract ResponseCode getErrorCode();

	public abstract HttpStatus getHttpStatus();
}
