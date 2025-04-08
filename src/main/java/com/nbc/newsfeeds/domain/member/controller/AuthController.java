package com.nbc.newsfeeds.domain.member.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.common.jwt.dto.TokenDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberCreateDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignInDto;
import com.nbc.newsfeeds.domain.member.dto.response.MemberDto;
import com.nbc.newsfeeds.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final MemberService memberService;

	@PostMapping("/signup")
	public ResponseEntity<MemberDto> create(@RequestBody MemberCreateDto memberCreateDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(memberService.saveMember(memberCreateDto));
	}

	@PostMapping("/signin")
	public ResponseEntity<TokenDto> signIn(@RequestBody MemberSignInDto memberSignInDto) {
		return ResponseEntity.ok(memberService.signIn(memberSignInDto, new Date()));
	}
}
