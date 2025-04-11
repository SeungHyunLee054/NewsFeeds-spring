package com.nbc.newsfeeds.domain.comment.code;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentExceptionCode implements ResponseCode {
	COMMENT_NOT_FOUND(false, HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),
	UNAUTHORIZED_ACCESS(false, HttpStatus.UNAUTHORIZED, "작성자가 아닙니다."),
	MEMBER_NOT_FOUND(false, HttpStatus.UNAUTHORIZED, "사용자 정보가 존재하지 않습니다."),
	HEART_COUNT_UNDERFLOW(false, HttpStatus.BAD_REQUEST, "좋아요 수는 0보다 작을 수 없습니다.");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}
