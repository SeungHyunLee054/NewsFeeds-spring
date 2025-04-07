package com.nbc.newsfeeds.domain.member.dto.request;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class MemberCreateDto {
	private String name;
	private String email;
	private String password;
	private LocalDate birth;
	private String phone;
}
