package com.nbc.newsfeeds.domain.heart.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HeartExceptionCode implements ResponseCode {

	USER_NOT_EXIST(false, HttpStatus.BAD_REQUEST, "해당 유저가 조회되지 않습니다."),
	FEED_NOT_EXIST(false, HttpStatus.BAD_REQUEST, "해당 피드가 조회되지 않습니다."),
	DUPLICATE_LIKE_REQUEST(false, HttpStatus.BAD_REQUEST, "해당 사용자가 피드에 좋아요를 이미 남겼습니다."),
	NO_EXISTING_LIKE(false, HttpStatus.BAD_REQUEST, "해당 사용자가 피드에 좋아요를 남긴 기록이 없습니다.");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}
