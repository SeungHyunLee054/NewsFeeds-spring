package com.nbc.newsfeeds.common.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;

@Getter
public class BizException extends BaseException {
	private final ResponseCode errorCode;
	private final HttpStatus httpStatus;

	public BizException(ResponseCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getHttpStatus();
	}
}
