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

	/**
	 * 회원 가입
	 * 유저 정보를 입력 받은 후 닉네임과 이메일 중복검증 후 저장 진행
	 * @param memberSignUpDto 닉네임, 이메일, 비밀번호, 생년월일, 전화번호
	 * @return 가입한 유저 정보
	 */
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

	/**
	 * 로그인
	 * 이메일과 비밀번호를 입력 받아 해당 유저가 탈퇴했는지 검증 후 비밀번호 검증 후 access token과 refresh token 발급
	 * @param memberSignInDto 이메일, 비밀번호
	 * @param date 로그인을 진행한 현재 날짜
	 * @return access token, refresh token
	 */
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

	/**
	 * 로그아웃
	 * 로그인한 access token을 black list로 지정 해당 토근 사용 불가 처리, 컨트롤러에서 해당 토큰의 principal 제거
	 * @param accessToken access token
	 * @param memberAuth 유저 정보가 담긴 principal
	 */
	public void signOut(String accessToken, MemberAuth memberAuth) {
		jwtService.blockAccessToken(accessToken, memberAuth);
	}

	/**
	 * 회원 탈퇴
	 * 유저의 탈퇴 상태를 delete = true로 설정, 닉네임과 민감 정보를 임의의 값으로 변경
	 * @param memberAuth 유저 정보
	 * @param password 비밀번호
	 * @return 탈퇴한 유저 id
	 */
	@Transactional
	public Long withdraw(MemberAuth memberAuth, String password) {
		Member member = memberRepository.findById(memberAuth.getId())
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));

		checkPassword(password, member.getPassword());

		member.withdraw();

		return member.getId();
	}

	/**
	 * access token 재발급
	 * 최초 로그인 시 발급했던 refresh token으로 access token 재발급
	 * @param refreshToken refresh token
	 * @return access token
	 */
	public AccessTokenDto regenerateAccessToken(String refreshToken) {
		String token = jwtService.regenerateAccessToken(refreshToken);

		return AccessTokenDto.from(token);
	}

	/**
	 * 유저 프로필 조회
	 * 조회하려는 유저가 본인이면 모든 정보 반환, 아닐 시 민감한 정보 제외 반환
	 * @param memberId 조회하려는 유저 id
	 * @param memberAuth 로그인한 유저의 정보
	 * @return 유저 정보
	 */
	public MemberDto getMemberProfile(Long memberId, MemberAuth memberAuth) {
		Member targetMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));

		return isSameMember(memberAuth.getId(), targetMember.getId())
			? privateProfile(targetMember) : publicProfile(targetMember);
	}

	/**
	 * 유저 본인의 정보 수정
	 * 닉네임과 비밀번호 수정, 수정 값이 둘 다 없는지 검증, 비밀번호 값이 기존과 동일한지 검증, 유저의 비밀번호가 맞는지 검증
	 * @param memberUpdateDto 닉네임, inner class(기존 비밀번호, 새로운 비밀번호) 적어도 한 개의 값은 존재해야 함
	 * @param memberAuth 로그인한 유저 정보
	 * @return 수정된 유저 정보
	 */
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
