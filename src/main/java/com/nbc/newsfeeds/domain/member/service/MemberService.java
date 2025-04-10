package com.nbc.newsfeeds.domain.member.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nbc.newsfeeds.common.jwt.core.JwtService;
import com.nbc.newsfeeds.common.jwt.dto.TokensDto;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignInDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignUpDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberUpdateDto;
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
	public MemberDto saveMember(MemberSignUpDto memberSignUpDto) {
		checkEmail(memberSignUpDto.getEmail());

		checkNickName(memberSignUpDto.getNickName());

		Member member = memberRepository.save(Member.builder()
			.nickName(memberSignUpDto.getNickName())
			.email(memberSignUpDto.getEmail())
			.password(passwordEncoder.encode(memberSignUpDto.getPassword()))
			.birth(memberSignUpDto.getBirth())
			.phone(memberSignUpDto.getPhone())
			.roles(List.of("ROLE_USER"))
			.build());

		return MemberDto.from(member);
	}

	@Transactional
	public TokensDto signIn(MemberSignInDto memberSignInDto, Date date) {
		Member member = memberRepository.findMemberByEmail(memberSignInDto.getEmail())
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));

		validateNotDeleted(member);

		checkPassword(memberSignInDto.getPassword(), member.getPassword());

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

		checkPassword(password, member.getPassword());

		member.withdraw();

		return member.getId();
	}

	public AccessTokenDto regenerateAccessToken(String refreshToken) {
		String token = jwtService.regenerateAccessToken(refreshToken);

		return AccessTokenDto.from(token);
	}

	public MemberDto getMemberProfile(Long memberId, MemberAuth memberAuth) {
		Member targetMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));

		return isSameMember(memberAuth.getId(), targetMember.getId())
			? privateProfile(targetMember) : publicProfile(targetMember);
	}

	@Transactional
	public MemberDto updateMemberProfile(MemberUpdateDto memberUpdateDto, MemberAuth memberAuth) {
		Member member = memberRepository.findById(memberAuth.getId())
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));

		validInput(memberUpdateDto);

		updateNickNameIfPresent(memberUpdateDto.getNickName(), member);

		updatePasswordIfPresent(memberUpdateDto.getPasswordUpdateForm(), member);

		return MemberDto.from(member);
	}

	private boolean isSameMember(Long targetMemberId, Long memberId) {
		return Objects.equals(targetMemberId, memberId);
	}

	private MemberDto privateProfile(Member targetMember) {
		return MemberDto.from(targetMember);
	}

	private MemberDto publicProfile(Member targetMember) {
		return MemberDto.builder()
			.id(targetMember.getId())
			.nickName(targetMember.getNickName())
			.email(targetMember.getEmail())
			.createdAt(targetMember.getCreatedAt())
			.modifiedAt(targetMember.getModifiedAt())
			.build();
	}

	private void checkEmail(String email) {
		if (memberRepository.existsByEmail(email)) {
			throw new MemberException(MemberResponseCode.ALREADY_EXISTS_EMAIL);
		}
	}

	private void checkNickName(String nickName) {
		if (memberRepository.existsByNickName(nickName)) {
			throw new MemberException(MemberResponseCode.ALREADY_EXISTS_NICKNAME);
		}
	}

	private void validateNotDeleted(Member member) {
		if (member.isDeleted()) {
			throw new MemberException(MemberResponseCode.WITHDRAWN_USER);
		}
	}

	private void checkPassword(String password, String encodedPassword) {
		if (!passwordEncoder.matches(password, encodedPassword)) {
			throw new MemberException(MemberResponseCode.WRONG_PASSWORD);
		}
	}

	private void validInput(MemberUpdateDto memberUpdateDto) {
		if (!StringUtils.hasText(memberUpdateDto.getNickName()) && memberUpdateDto.getPasswordUpdateForm() == null) {
			throw new MemberException(MemberResponseCode.NOT_CHANGED);
		}
	}

	private void updateNickNameIfPresent(String newNickName, Member member) {
		if (!StringUtils.hasText(newNickName)) {
			return;
		}

		checkNickName(newNickName);

		member.changeNickName(newNickName);
	}

	private void updatePasswordIfPresent(MemberUpdateDto.PasswordUpdateForm passwordUpdateForm, Member member) {
		if (passwordUpdateForm == null) {
			return;
		}

		checkPassword(passwordUpdateForm.getPassword(), member.getPassword());

		if (passwordUpdateForm.isSameAsCurrentPassword(member.getPassword(), passwordEncoder)) {
			throw new MemberException(MemberResponseCode.SAME_PASSWORD);
		}

		member.changePassword(passwordUpdateForm.getNewPassword());
	}

}
