package com.nbc.newsfeeds.domain.member.event;

import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberWithdrawEvent {
	private final String accessToken;
	private final MemberAuth memberAuth;
}
