package com.nbc.newsfeeds.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessTokenDto {
	private String accessToken;

	public static AccessTokenDto from(String accessToken) {
		return AccessTokenDto.builder()
			.accessToken(accessToken)
			.build();
	}
}
