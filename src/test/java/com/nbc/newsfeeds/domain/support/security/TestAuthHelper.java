package com.nbc.newsfeeds.domain.support.security;


import java.util.List;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

public class TestAuthHelper {

	public static RequestPostProcessor authUser(Long id) {
		return SecurityMockMvcRequestPostProcessors.user(MemberAuth.builder()
			.id(id)
			.email("mock" + id + "@test.com")
			.roles(List.of("ROLE_USER"))
			.build());
	}
}