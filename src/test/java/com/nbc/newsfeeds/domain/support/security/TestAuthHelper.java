package com.nbc.newsfeeds.domain.support.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

public class TestAuthHelper {

	public static RequestPostProcessor customAuth(Long memberId) {
		MemberAuth memberAuth = new MemberAuth(memberId, "", List.of("ROLE_USER"));
		List<SimpleGrantedAuthority> authorities = memberAuth.getRoles().stream()
			.map(SimpleGrantedAuthority::new)
			.toList();
		var auth = new UsernamePasswordAuthenticationToken(
			memberAuth,
			null,
			authorities
		);
		return authentication(auth);
	}
}
