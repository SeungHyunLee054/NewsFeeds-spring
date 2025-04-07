package com.nbc.newsfeeds.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberAuthDto {
	private Long id;
	private String email;
}
