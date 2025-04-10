package com.nbc.newsfeeds.domain.member.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nbc.newsfeeds.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDto {
	private Long id;
	private String nickName;
	private String email;
	private LocalDate birth;
	private String phone;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	public static MemberDto from(Member member) {
		return MemberDto.builder()
			.id(member.getId())
			.nickName(member.getNickName())
			.email(member.getEmail())
			.birth(member.getBirth())
			.phone(member.getPhone())
			.createdAt(member.getCreatedAt())
			.modifiedAt(member.getModifiedAt())
			.build();
	}
}
