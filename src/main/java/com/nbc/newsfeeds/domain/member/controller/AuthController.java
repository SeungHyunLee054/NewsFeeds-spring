package com.nbc.newsfeeds.domain.member.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.common.jwt.dto.TokensDto;
import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.common.util.SecurityUtils;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignInDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignUpDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberWithdrawDto;
import com.nbc.newsfeeds.domain.member.dto.response.AccessTokenDto;
import com.nbc.newsfeeds.domain.member.dto.response.MemberDto;
import com.nbc.newsfeeds.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final MemberService memberService;

	@Operation(summary = "회원가입")
	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<MemberDto>> create(@Valid @RequestBody MemberSignUpDto memberSignUpDto) {
		MemberDto memberDto = memberService.saveMember(memberSignUpDto);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(CommonResponse.of(MemberResponseCode.SUCCESS_SIGN_UP, memberDto));
	}

	@Operation(summary = "로그인")
	@PostMapping("/signin")
	public ResponseEntity<CommonResponse<TokensDto>> signIn(@Valid @RequestBody MemberSignInDto memberSignInDto) {
		TokensDto tokensDto = memberService.signIn(memberSignInDto, new Date());

		return ResponseEntity.ok(CommonResponse.of(MemberResponseCode.SUCCESS_SIGN_IN, tokensDto));
	}

	@Operation(summary = "로그 아웃", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping("/signout")
	public ResponseEntity<CommonResponse<Object>> signOut(@AuthenticationPrincipal MemberAuth memberAuth) {
		String accessToken = SecurityUtils.getCurrentToken();
		memberService.signOut(accessToken, memberAuth);
		SecurityUtils.clearContext();

		return ResponseEntity.ok(CommonResponse.from(MemberResponseCode.SUCCESS_SIGN_OUT));
	}

	@Operation(summary = "회원 탈퇴", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/withdraw")
	public ResponseEntity<CommonResponse<Long>> withdraw(@AuthenticationPrincipal MemberAuth memberAuth,
		@Valid @RequestBody MemberWithdrawDto memberWithdrawDto) {
		String accessToken = SecurityUtils.getCurrentToken();
		Long memberId = memberService.withdraw(memberAuth, memberWithdrawDto.getPassword(), accessToken);
		SecurityUtils.clearContext();

		return ResponseEntity.ok(CommonResponse.of(MemberResponseCode.SUCCESS_WITHDRAW, memberId));
	}

	@Operation(summary = "토큰 재발급")
	@PostMapping("/reissue")
	public ResponseEntity<CommonResponse<AccessTokenDto>> reissueAccessToken() {
		String refreshToken = SecurityUtils.getCurrentToken();
		AccessTokenDto accessTokenDto = memberService.regenerateAccessToken(refreshToken);

		return ResponseEntity.ok(CommonResponse.of(MemberResponseCode.SUCCESS_REGENERATE_ACCESS_TOKEN, accessTokenDto));
	}
}
