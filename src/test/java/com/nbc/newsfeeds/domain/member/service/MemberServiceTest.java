package com.nbc.newsfeeds.domain.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private MemberService memberService;

	@Spy
	private Member member;

	@Spy
	private MemberCreateDto memberCreateDto;

	@Spy
	private MemberSignInDto memberSignInDto;

	@Mock
	private TokensDto tokensDto;

	@Spy
	private MemberAuth memberAuth;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.id(1L)
			.nickName("test")
			.email("test@test")
			.password("encodedPassword")
			.birth(LocalDate.now())
			.phone("01012345678")
			.roles(List.of("ROLE_USER"))
			.isDeleted(false)
			.build();

		memberCreateDto = new MemberCreateDto("test", "test@test", "testPass",
			LocalDate.now(), "01012345678");

		memberSignInDto = new MemberSignInDto("test@test", "testPass");

		memberAuth = new MemberAuth(1L, "test@test", List.of("ROLE_USER"));

	}

	@Nested
	@DisplayName("회원 가입 테스트")
	class SignUpTest {
		@Test
		@DisplayName("회원가입 성공")
		void success_saveMember() {
			// Given
			given(memberRepository.existsByEmail(anyString()))
				.willReturn(false);
			given(passwordEncoder.encode(anyString()))
				.willReturn("encodedPassword");
			given(memberRepository.save(any()))
				.willReturn(member);

			// When
			MemberDto memberDto = memberService.saveMember(memberCreateDto);

			// Then
			verify(memberRepository, times(1)).save(any());
			assertAll(
				() -> status().isCreated(),
				() -> assertEquals(memberCreateDto.getNickName(), memberDto.getNickName()),
				() -> assertEquals(memberCreateDto.getEmail(), memberDto.getEmail()),
				() -> assertEquals(memberCreateDto.getBirth(), memberDto.getBirth()),
				() -> assertEquals(memberCreateDto.getPhone(), memberDto.getPhone())
			);
		}

		@Test
		@DisplayName("회원가입 실패 - 이미 존재하는 이메일")
		void fail_saveMember_alreadyExistEmail() {
			// Given
			given(memberRepository.existsByEmail(anyString()))
				.willReturn(true);

			// When
			MemberException exception =
				assertThrows(MemberException.class, () -> memberService.saveMember(memberCreateDto));

			// Then
			assertAll(
				() -> assertEquals(MemberResponseCode.ALREADY_EXISTS_EMAIL.getHttpStatus(),
					exception.getHttpStatus()),
				() -> assertEquals(MemberResponseCode.ALREADY_EXISTS_EMAIL.getMessage(),
					exception.getErrorMessage())
			);

		}

		@Test
		@DisplayName("회원가입 실패 - 이미 존재하는 이메일")
		void fail_saveMember_alreadyExistNickName() {
			// Given
			given(memberRepository.existsByEmail(anyString()))
				.willReturn(false);
			given(memberRepository.existsByNickName(anyString()))
				.willReturn(true);

			// When
			MemberException exception =
				assertThrows(MemberException.class, () -> memberService.saveMember(memberCreateDto));

			// Then
			assertAll(
				() -> assertEquals(MemberResponseCode.ALREADY_EXISTS_NICKNAME.getHttpStatus(),
					exception.getHttpStatus()),
				() -> assertEquals(MemberResponseCode.ALREADY_EXISTS_NICKNAME.getMessage(),
					exception.getErrorMessage())
			);

		}
	}

	@Nested
	@DisplayName("로그인 테스트")
	class SignInTest {
		@Test
		@DisplayName("로그인 성공")
		void success_signIn() {
			// Given
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(true);
			given(memberRepository.findMemberByEmail(anyString()))
				.willReturn(Optional.ofNullable(member));
			given(tokensDto.getAccessToken())
				.willReturn("accessToken");
			given(tokensDto.getRefreshToken())
				.willReturn("refreshToken");
			given(jwtService.issueToken(any(), any()))
				.willReturn(tokensDto);

			// When
			TokensDto result = memberService.signIn(memberSignInDto, new Date());

			// Then
			assertAll(
				() -> status().isOk(),
				() -> assertEquals("accessToken", result.getAccessToken()),
				() -> assertEquals("refreshToken", result.getRefreshToken())
			);

		}

		@Test
		@DisplayName("로그인 실패 - 유저를 찾을 수 없음")
		void fail_signIn_memberNotFound() {
			// Given
			given(memberRepository.findMemberByEmail(anyString()))
				.willReturn(Optional.empty());

			// When
			MemberException exception = assertThrows(MemberException.class,
				() -> memberService.signIn(memberSignInDto, new Date()));

			// Then
			assertAll(
				() -> assertEquals(MemberResponseCode.MEMBER_NOT_FOUND.getHttpStatus(),
					exception.getHttpStatus()),
				() -> assertEquals(MemberResponseCode.MEMBER_NOT_FOUND.getMessage(),
					exception.getErrorMessage())
			);

		}

		@Test
		@DisplayName("로그인 실패 - 탈퇴한 유저")
		void fail_signIn_withdrawnMember() {
			// Given
			given(memberRepository.findMemberByEmail(anyString()))
				.willReturn(Optional.ofNullable(member.toBuilder()
					.isDeleted(true)
					.build()));

			// When
			MemberException exception = assertThrows(MemberException.class,
				() -> memberService.signIn(memberSignInDto, new Date()));

			// Then
			assertAll(
				() -> assertEquals(MemberResponseCode.WITHDRAWN_USER.getHttpStatus(),
					exception.getHttpStatus()),
				() -> assertEquals(MemberResponseCode.WITHDRAWN_USER.getMessage(),
					exception.getErrorMessage())
			);

		}

		@Test
		@DisplayName("로그인 실패 - 비밀번호 오류")
		void fail_signIn_wrongPassword() {
			// Given
			given(memberRepository.findMemberByEmail(anyString()))
				.willReturn(Optional.ofNullable(member));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(false);

			// When
			MemberException exception = assertThrows(MemberException.class,
				() -> memberService.signIn(memberSignInDto, new Date()));

			// Then
			assertAll(
				() -> assertEquals(MemberResponseCode.WRONG_PASSWORD.getHttpStatus(),
					exception.getHttpStatus()),
				() -> assertEquals(MemberResponseCode.WRONG_PASSWORD.getMessage(),
					exception.getErrorMessage())
			);

		}
	}

	@Nested
	@DisplayName("로그 아웃 테스트")
	class SignOutTest {
		@Test
		@DisplayName("로그 아웃 성공")
		void success_signOut() {
			// Given

			// When
			memberService.signOut("accessToken", memberAuth);

			// Then
			assertAll(
				() -> status().isOk()
			);

		}
	}

	@Nested
	@DisplayName("회원탈퇴 테스트")
	class WithdrawTest {
		@Test
		@DisplayName("회원탈퇴 성공")
		void success_withdraw() {
			// Given
			given(memberRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(member));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(true);

			// When
			Long memberId = memberService.withdraw(memberAuth, "testPass");

			// Then
			assertAll(
				() -> status().isOk(),
				() -> assertEquals(memberAuth.getId(), memberId)
			);

		}

		@Test
		@DisplayName("회원 탈퇴 실패 - 유저를 찾을 수 없음")
		void fail_withdraw_memberNotFound() {
			// Given
			given(memberRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			// When
			MemberException exception = assertThrows(MemberException.class,
				() -> memberService.withdraw(memberAuth, "testPass"));

			// Then
			assertAll(
				() -> assertEquals(MemberResponseCode.MEMBER_NOT_FOUND.getHttpStatus(),
					exception.getHttpStatus()),
				() -> assertEquals(MemberResponseCode.MEMBER_NOT_FOUND.getMessage(),
					exception.getErrorMessage())
			);

		}

		@Test
		@DisplayName("회원 탈퇴 실패 - 비밀번호 오류")
		void fail_withdraw_wrongPassword() {
			// Given
			given(memberRepository.findById(anyLong()))
				.willReturn(Optional.ofNullable(member));
			given(passwordEncoder.matches(anyString(), anyString()))
				.willReturn(false);

			// When
			MemberException exception = assertThrows(MemberException.class,
				() -> memberService.withdraw(memberAuth, "testPass"));

			// Then
			assertAll(
				() -> assertEquals(MemberResponseCode.WRONG_PASSWORD.getHttpStatus(),
					exception.getHttpStatus()),
				() -> assertEquals(MemberResponseCode.WRONG_PASSWORD.getMessage(),
					exception.getErrorMessage())
			);

		}

	}

	@Nested
	@DisplayName("access token 재발급 테스트")
	class RegenerateAccessTokenTest {
		@Test
		@DisplayName("access token 재발급 성공")
		void success_regenerateAccessToken() {
			// Given
			given(jwtService.regenerateAccessToken(anyString()))
				.willReturn("accessToken");

			// When
			AccessTokenDto accessTokenDto = memberService.regenerateAccessToken("refreshToken");

			// Then
			assertAll(
				() -> status().isOk(),
				() -> assertEquals("accessToken", accessTokenDto.getAccessToken())
			);

		}
	}
}
