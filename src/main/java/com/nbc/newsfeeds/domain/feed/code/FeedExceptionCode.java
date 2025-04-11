package com.nbc.newsfeeds.domain.feed.code;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedExceptionCode implements ResponseCode {
	/** 요청한 게시글이 존재하지 않음 */
	FEED_NOT_FOUND(false, HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

	/** 로그인한 사용자가 해당 게시글의 작성자가 아님 */
	NOT_FEED_OWNER(false, HttpStatus.FORBIDDEN, "본인의 게시글만 수정/삭제할 수 있습니다."),

	/** 좋아요 수가 0보다 작아질 수 없음 */
	HEART_COUNT_UNDERFLOW(false, HttpStatus.UNAUTHORIZED, "좋아요 수는 0보다 작을 수 없습니다."),

	/** 댓글 수가 0보다 작아질 수 없음 */
	COMMENT_COUNT_UNDERFLOW(false, HttpStatus.UNAUTHORIZED, "댓글 수는 0보다 작을 수 없습니다.");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}
