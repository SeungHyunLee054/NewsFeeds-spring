package com.nbc.newsfeeds.domain.friend.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;
import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;

@Getter
public class FriendBizException extends BaseException {
	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public FriendBizException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
