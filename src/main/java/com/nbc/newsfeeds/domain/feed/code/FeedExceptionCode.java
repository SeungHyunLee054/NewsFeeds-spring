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
	COMMENT_COUNT_UNDERFLOW(false, HttpStatus.UNAUTHORIZED, "댓글 수는 0보다 작을 수 없습니다."),

	/** 정렬 기준 예외 */
	INVALID_SORT_TYPE(false, HttpStatus.BAD_REQUEST, "정렬 기준은 latest, likes, comments 중 하나여야 합니다."),

	/** 시작일이 종료일 보다 이전일 시 예외 */
	INVALID_DATE_RANGE(false, HttpStatus.BAD_REQUEST, "시작일은 종료일보다 이전이어야 합니다.");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}
