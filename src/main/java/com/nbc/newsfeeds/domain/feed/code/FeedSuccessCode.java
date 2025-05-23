package com.nbc.newsfeeds.domain.feed.code;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.model.response.ResponseCode;

import lombok.Getter;

@Getter
public enum FeedSuccessCode implements ResponseCode {

	/** 피드 생성 성공 */
	FEED_CREATED(true, HttpStatus.CREATED, "피드 생성 성공"),

	/** 피드 단건 조회 성공 */
	FEED_FOUND(true, HttpStatus.OK, "피드 조회 성공"),

	/** 피드 목록 조회 성공 */
	FEED_LISTED(true, HttpStatus.OK, "피드 목록 조회 성공"),

	/** 좋아요 누른 피드 목록 조회 성공 */
	FEED_LISTED_LIKE(true, HttpStatus.OK, "좋아요 피드 목록 조회 성공"),

	/** 피드 수정 성공 */
	FEED_UPDATED(true, HttpStatus.OK, "피드 수정 성공"),

	/** 피드 삭제 성공 */
	FEED_DELETED(true, HttpStatus.OK, "피드 삭제 성공");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;

	FeedSuccessCode(boolean success, HttpStatus httpStatus, String message) {
		this.success = success;
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
