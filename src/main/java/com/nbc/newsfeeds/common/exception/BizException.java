package com.nbc.newsfeeds.common.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;

@Getter
public class BizException extends BaseException {
	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public BizException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
