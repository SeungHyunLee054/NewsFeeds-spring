package com.nbc.newsfeeds.domain.comment.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;
import com.nbc.newsfeeds.common.model.response.ResponseCode;

import lombok.Getter;

@Getter
public class CommentException extends BaseException {

	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public CommentException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
