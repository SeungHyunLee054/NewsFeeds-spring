package com.nbc.newsfeeds.common.model.response;

import org.springframework.http.HttpStatus;

public interface ResponseCode {
	boolean isSuccess();

	HttpStatus getHttpStatus();

	String getMessage();
}
