package com.nbc.newsfeeds.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.dto.request.MemberUpdateDto;
import com.nbc.newsfeeds.domain.member.dto.response.MemberDto;
import com.nbc.newsfeeds.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {
	private final MemberService memberService;

	@Operation(summary = "유저 프로필 조회", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/{memberId}")
	public ResponseEntity<CommonResponse<MemberDto>> getMemberProfile(@PathVariable("memberId") @Min(1) Long memberId,
		@AuthenticationPrincipal MemberAuth memberAuth) {
		MemberDto memberDto = memberService.getMemberProfile(memberId, memberAuth);

		return ResponseEntity.ok()
			.body(CommonResponse.of(MemberResponseCode.SUCCESS_GET_MEMBER_PROFILE, memberDto));
	}

	@Operation(summary = "유저 수정", security = {@SecurityRequirement(name = "bearer-key")})
	@PatchMapping
	public ResponseEntity<CommonResponse<MemberDto>> updateMemberProfile(
		@Valid @RequestBody MemberUpdateDto memberUpdateDto,
		@AuthenticationPrincipal MemberAuth memberAuth) {
		MemberDto memberDto = memberService.updateMemberProfile(memberUpdateDto, memberAuth);

		return ResponseEntity.ok()
			.body(CommonResponse.of(MemberResponseCode.SUCCESS_UPDATE_MEMBER_PROFILE, memberDto));
	}
}
