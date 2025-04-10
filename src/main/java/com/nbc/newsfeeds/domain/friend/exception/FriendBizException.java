package com.nbc.newsfeeds.domain.friend.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;

import lombok.Getter;

@Getter
public class FriendBizException extends BaseException {
	private final FriendExceptionCode responseCode;
	private final HttpStatus httpStatus;

	public FriendBizException(FriendExceptionCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
