package com.nbc.newsfeeds.domain.feed.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;
import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;

@Getter
public class FeedBizException extends BaseException {
	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public FeedBizException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
