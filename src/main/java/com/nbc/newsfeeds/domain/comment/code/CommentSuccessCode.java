package com.nbc.newsfeeds.domain.comment.code;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.model.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentSuccessCode implements ResponseCode {
	COMMENT_CREATE_SUCCESS(true, HttpStatus.CREATED, "댓글 생성 성공"),
	COMMENT_GET_SUCCESS(true, HttpStatus.OK, "댓글 단건 조회 성공"),
	COMMENT_LIST_SUCCESS(true, HttpStatus.OK, "댓글 목록 조회 성공"),
	COMMENT_UPDATE_SUCCESS(true, HttpStatus.OK, "댓글 수정 성공"),
	COMMENT_DELETE_SUCCESS(true, HttpStatus.OK, "댓글 삭제 성공");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;

}
