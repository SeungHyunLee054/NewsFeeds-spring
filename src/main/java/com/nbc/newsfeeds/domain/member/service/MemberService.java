package com.nbc.newsfeeds.domain.member.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nbc.newsfeeds.common.jwt.core.JwtService;
import com.nbc.newsfeeds.common.jwt.dto.TokensDto;
import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberCreateDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignInDto;
import com.nbc.newsfeeds.domain.member.dto.response.MemberDto;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.exception.MemberException;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public CommonResponse<MemberDto> saveMember(MemberCreateDto memberCreateDto) {
		if (memberRepository.existsByEmail(memberCreateDto.getEmail())) {
			throw new MemberException(MemberResponseCode.ALREADY_EXISTS_EMAIL);
		}

		Member member = memberRepository.save(Member.builder()
			.nickName(memberCreateDto.getNickName())
			.email(memberCreateDto.getEmail())
			.password(passwordEncoder.encode(memberCreateDto.getPassword()))
			.birth(memberCreateDto.getBirth())
			.phone(memberCreateDto.getPhone())
			.roles(List.of("ROLE_USER"))
			.build());

		MemberDto memberDto = MemberDto.from(member);

		return CommonResponse.of(MemberResponseCode.SUCCESS_SIGN_UP, memberDto);
	}

	@Transactional
	public CommonResponse<TokensDto> signIn(MemberSignInDto memberSignInDto, Date date) {
		Member member = memberRepository.findMemberByEmail(memberSignInDto.getEmail())
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));

		member.validateNotDeleted();

		member.checkPassword(passwordEncoder, memberSignInDto.getPassword());

		TokensDto tokensDto = jwtService.issueToken(MemberAuthDto.builder()
			.id(member.getId())
			.email(member.getEmail())
			.roles(member.getRoles())
			.build(), date);
		return CommonResponse.of(MemberResponseCode.SUCCESS_SIGN_IN, tokensDto);
	}

	public CommonResponse<Object> signOut(String accessToken, MemberAuthDto memberAuthDto) {
		jwtService.blockAccessToken(accessToken, memberAuthDto);

		return CommonResponse.from(MemberResponseCode.SUCCESS_SIGN_OUT);
	}

	@Transactional
	public CommonResponse<Long> withdraw(MemberAuthDto memberAuthDto, String password) {
		Member member = memberRepository.findById(memberAuthDto.getId())
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));

		member.checkPassword(passwordEncoder, password);

		member.withdraw();

		Long memberId = member.getId();

		return CommonResponse.of(MemberResponseCode.SUCCESS_WITHDRAW, memberId);
	}

	public CommonResponse<String> regenerateAccessToken(String refreshToken) {
		String token = jwtService.regenerateAccessToken(refreshToken);

		return CommonResponse.of(MemberResponseCode.SUCCESS_REGENERATE_ACCESS_TOKEN, token);
	}
}
