package com.nbc.newsfeeds.domain.member.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.common.jwt.dto.TokensDto;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberCreateDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberDeleteDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignInDto;
import com.nbc.newsfeeds.domain.member.dto.response.MemberDto;
import com.nbc.newsfeeds.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final MemberService memberService;

	@Operation(summary = "회원가입")
	@PostMapping("/signup")
	public ResponseEntity<MemberDto> create(@RequestBody MemberCreateDto memberCreateDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(memberService.saveMember(memberCreateDto));
	}

	@Operation(summary = "로그인")
	@PostMapping("/signin")
	public ResponseEntity<TokensDto> signIn(@RequestBody MemberSignInDto memberSignInDto) {
		return ResponseEntity.ok(memberService.signIn(memberSignInDto, new Date()));
	}

	@Operation(summary = "로그 아웃", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping("/signout")
	public ResponseEntity<?> signOut(@AuthenticationPrincipal MemberAuthDto memberAuthDto) {
		String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
		memberService.signOut(token, memberAuthDto);
		SecurityContextHolder.clearContext();

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "회원 탈퇴", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/withdraw")
	public ResponseEntity<?> withdraw(@AuthenticationPrincipal MemberAuthDto memberAuthDto,
		@RequestBody MemberDeleteDto memberDeleteDto) {
		memberService.withdraw(memberAuthDto, memberDeleteDto.getPassword());

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "토큰 재발급")
	@PostMapping("/reissue")
	public ResponseEntity<?> reissueAccessToken() {
		String refreshToken = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
		String token = memberService.regenerateAccessToken(refreshToken);

		return ResponseEntity.ok(token);
	}
}
