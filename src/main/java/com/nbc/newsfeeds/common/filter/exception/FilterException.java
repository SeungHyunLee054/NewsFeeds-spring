package com.nbc.newsfeeds.common.filter.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;

import lombok.Getter;

@Getter
public class FilterException extends BaseException {
	private final FilterExceptionCode responseCode;
	private final HttpStatus httpStatus;

	public FilterException(FilterExceptionCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
