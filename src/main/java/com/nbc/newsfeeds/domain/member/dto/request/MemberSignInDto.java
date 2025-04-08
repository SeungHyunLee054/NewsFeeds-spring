package com.nbc.newsfeeds.domain.member.dto.request;

import lombok.Getter;

@Getter
public class MemberSignInDto {
	private String email;
	private String password;
}
