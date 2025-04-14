package com.nbc.newsfeeds.common.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.model.response.ResponseCode;

public abstract class BaseException extends RuntimeException {
	public abstract ResponseCode getResponseCode();

	public abstract HttpStatus getHttpStatus();
}
