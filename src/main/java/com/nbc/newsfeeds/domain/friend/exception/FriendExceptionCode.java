package com.nbc.newsfeeds.domain.friend.exception;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendExceptionCode implements ResponseCode {
	ALREADY_REQUESTED(false, HttpStatus.CONFLICT, "이미 신청이 되어있습니다."),
	ALREADY_FRIENDS(false, HttpStatus.CONFLICT, "이미 친구입니다."),
	FRIEND_REQUEST_NOT_FOUND(false, HttpStatus.NOT_FOUND, "존재하지 않는 친구 요청입니다."),
	CANNOT_REQUEST_SELF(false, HttpStatus.BAD_REQUEST, "자기 자신과 친구가 될 수 없습니다."),
	ALREADY_PROCESSED_REQUEST(false, HttpStatus.CONFLICT, "이미 처리된 친구 요청입니다."),
	NOT_ACCEPTED_REQUEST(false, HttpStatus.CONFLICT, "수락된 친구 요청이 아닙니다."),
	NOT_FRIEND_REQUEST_RECEIVER(false, HttpStatus.FORBIDDEN, "본인이 받은 친구 요청이 아닙니다."),
	NOT_FRIEND_PARTICIPANT(false, HttpStatus.FORBIDDEN, "본인의 친구가 아닙니다."),
	NOT_FRIEND_REQUEST_SENDER(false, HttpStatus.FORBIDDEN, "본인이 요청한 친구 요청이 아닙니다.");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
}

