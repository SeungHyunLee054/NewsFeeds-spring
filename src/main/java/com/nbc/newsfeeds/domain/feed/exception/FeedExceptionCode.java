package com.nbc.newsfeeds.domain.feed.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedExceptionCode implements ResponseCode {
	FEED_NOT_FOUND(false, HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
	NOT_FEED_OWNER(false, HttpStatus.FORBIDDEN, "본인의 게시글만 수정/삭제할 수 있습니다.");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}
