package com.nbc.newsfeeds.domain.comment.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;
import com.nbc.newsfeeds.common.response.ResponseCode;

public class CommentException extends BaseException {

	private final CommentExceptionCode code;

	public CommentException(CommentExceptionCode code) {
		super();
		this.code = code;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return code.getHttpStatus();
	}

	@Override
	public ResponseCode getErrorCode() {
		return code;
	}
}
