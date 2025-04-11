package com.nbc.newsfeeds.domain.heart.dto;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HeartResponseCode implements ResponseCode {
	HEART_CREATED(true, HttpStatus.CREATED, "좋아요 추가 성공."),
	HEART_REMOVED(true, HttpStatus.OK, "좋아요 취소 성공."),
	HEART_RETRIEVED(true, HttpStatus.OK, "좋아요 조회 성공");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}
