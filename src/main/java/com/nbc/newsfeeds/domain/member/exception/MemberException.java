package com.nbc.newsfeeds.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;

import lombok.Getter;

@Getter
public class MemberException extends BaseException {
	private final MemberResponseCode responseCode;
	private final HttpStatus httpStatus;

	public MemberException(MemberResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
