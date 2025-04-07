package com.nbc.newsfeeds.domain.member.service;

import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nbc.newsfeeds.common.jwt.JwtTokenProvider;
import com.nbc.newsfeeds.common.jwt.dto.TokenDto;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberCreateDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignInDto;
import com.nbc.newsfeeds.domain.member.dto.response.MemberDto;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public MemberDto saveMember(MemberCreateDto memberCreateDto) {
		if (memberRepository.existsByEmail(memberCreateDto.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		Member member = memberRepository.save(Member.builder()
			.nickName(memberCreateDto.getName())
			.email(memberCreateDto.getEmail())
			.password(passwordEncoder.encode(memberCreateDto.getPassword()))
			.birth(memberCreateDto.getBirth())
			.phone(memberCreateDto.getPhone())
			.build());

		return MemberDto.from(member);
	}

	public TokenDto signIn(MemberSignInDto memberSignInDto, Date date) {
		Member member = memberRepository.findMemberByEmail(memberSignInDto.getEmail())
			.orElseThrow(() -> new RuntimeException("Email not found"));

		if (!passwordEncoder.matches(memberSignInDto.getPassword(), member.getPassword())) {
			throw new RuntimeException("Wrong password");
		}

		return jwtTokenProvider.getToken(MemberAuthDto.builder()
			.id(member.getId())
			.email(member.getEmail())
			.build(), date);
	}
}
