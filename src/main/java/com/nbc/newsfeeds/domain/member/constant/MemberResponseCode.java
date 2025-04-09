package com.nbc.newsfeeds.domain.member.constant;

import org.springframework.http.HttpStatus;

import com.nbc.newsfeeds.common.response.ResponseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemberResponseCode implements ResponseCode {
	SUCCESS_SIGN_UP(true, HttpStatus.CREATED, "회원 가입 성공"),
	SUCCESS_SIGN_IN(true, HttpStatus.OK, "로그인 성공"),
	SUCCESS_SIGN_OUT(true, HttpStatus.OK, "로그아웃 성공"),
	SUCCESS_WITHDRAW(true, HttpStatus.OK, "탈퇴 성공"),
	SUCCESS_REGENERATE_ACCESS_TOKEN(true, HttpStatus.OK, "access token 재발급 성공"),

	FAIL_SIGN_IN(false, HttpStatus.UNAUTHORIZED, "로그인에 실패하였습니다."),
	MEMBER_NOT_FOUND(false, HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
	ALREADY_EXISTS_EMAIL(false, HttpStatus.CONFLICT, "이미 존재하는 이메일이 있습니다."),
	WITHDRAWN_USER(false, HttpStatus.UNAUTHORIZED, "이미 탈퇴한 유저입니다.");

	private final boolean isSuccess;
	private final HttpStatus httpStatus;
	private final String message;
}
