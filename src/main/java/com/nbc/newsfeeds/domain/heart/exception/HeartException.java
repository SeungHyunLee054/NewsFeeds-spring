package com.nbc.newsfeeds.domain.heart.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;
import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;

@Getter
public class HeartException extends BaseException {

	private final ResponseCode errorCode;
	private final HttpStatus httpStatus;

	public HeartException(ResponseCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getHttpStatus();
	}
}
