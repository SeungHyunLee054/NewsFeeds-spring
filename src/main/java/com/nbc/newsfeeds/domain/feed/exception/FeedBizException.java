package com.nbc.newsfeeds.domain.feed.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.exception.BaseException;
import com.nbc.newsfeeds.common.model.response.ResponseCode;

import lombok.Getter;

@Getter
public class FeedBizException extends BaseException {
	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	/**
	 * FeedBizException 생성자
	 *
	 * @param responseCode 예외에 해당하는 응답 코드(에러 메세지, 상태 코드 포함)
	 * @author 기원
	 */
	public FeedBizException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
