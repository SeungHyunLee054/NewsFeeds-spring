package com.nbc.newsfeeds.domain.heart.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HeartExceptionCode implements ResponseCode {

	DUPLICATE_LIKE_REQUEST(false, HttpStatus.BAD_REQUEST, "해당 사용자가 좋아요를 이미 남겼습니다."),
	COMMENT_HEART_MISMATCH_EXCEPTION(false, HttpStatus.BAD_REQUEST, "해당 게시글에 속한 댓글이 아닙니다."),
	NO_EXISTING_LIKE(false, HttpStatus.BAD_REQUEST, "해당 사용자가 좋아요를 남긴 기록이 없습니다.");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}
