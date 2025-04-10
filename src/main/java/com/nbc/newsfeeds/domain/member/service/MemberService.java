package com.nbc.newsfeeds.domain.member.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nbc.newsfeeds.common.jwt.core.JwtService;
import com.nbc.newsfeeds.common.jwt.dto.TokensDto;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.dto.request.MemberCreateDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignInDto;
import com.nbc.newsfeeds.domain.member.dto.response.AccessTokenDto;
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
	public MemberDto saveMember(MemberCreateDto memberCreateDto) {
		if (memberRepository.existsByEmail(memberCreateDto.getEmail())) {
			throw new MemberException(MemberResponseCode.ALREADY_EXISTS_EMAIL);
		}
		if (memberRepository.existsByNickName(memberCreateDto.getNickName())) {
			throw new MemberException(MemberResponseCode.ALREADY_EXISTS_NICKNAME);
		}

		Member member = memberRepository.save(Member.builder()
			.nickName(memberCreateDto.getNickName())
			.email(memberCreateDto.getEmail())
			.password(passwordEncoder.encode(memberCreateDto.getPassword()))
			.birth(memberCreateDto.getBirth())
			.phone(memberCreateDto.getPhone())
			.roles(List.of("ROLE_USER"))
			.build());

		return MemberDto.from(member);
	}

	@Transactional
	public TokensDto signIn(MemberSignInDto memberSignInDto, Date date) {
		Member member = memberRepository.findMemberByEmail(memberSignInDto.getEmail())
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));

		member.validateNotDeleted();

		member.checkPassword(passwordEncoder, memberSignInDto.getPassword());

		return jwtService.issueToken(MemberAuth.builder()
			.id(member.getId())
			.email(member.getEmail())
			.roles(member.getRoles())
			.build(), date);
	}

	public void signOut(String accessToken, MemberAuth memberAuth) {
		jwtService.blockAccessToken(accessToken, memberAuth);
	}

	@Transactional
	public Long withdraw(MemberAuth memberAuth, String password) {
		Member member = memberRepository.findById(memberAuth.getId())
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));

		member.checkPassword(passwordEncoder, password);

		member.withdraw();

		return member.getId();
	}

	public AccessTokenDto regenerateAccessToken(String refreshToken) {
		String token = jwtService.regenerateAccessToken(refreshToken);

		return AccessTokenDto.from(token);
	}
}
