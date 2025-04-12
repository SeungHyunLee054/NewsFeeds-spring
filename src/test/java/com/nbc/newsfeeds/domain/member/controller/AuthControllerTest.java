package com.nbc.newsfeeds.domain.member.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.common.jwt.dto.TokensDto;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignInDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberSignUpDto;
import com.nbc.newsfeeds.domain.member.dto.request.MemberWithdrawDto;
import com.nbc.newsfeeds.domain.member.dto.response.AccessTokenDto;
import com.nbc.newsfeeds.domain.member.dto.response.MemberDto;
import com.nbc.newsfeeds.domain.member.service.MemberService;
import com.nbc.newsfeeds.domain.support.security.TestSecurityConfig;

@WebMvcTest(controllers = AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {
	@MockitoBean
	private MemberService memberService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Spy
	private MemberSignUpDto memberSignUpDto;

	@Spy
	private MemberDto memberDto;

	@Spy
	private MemberSignInDto memberSignInDto;

	@Spy
	private MemberWithdrawDto memberWithdrawDto;

	@Spy
	private AccessTokenDto accessTokenDto;

	@Mock
	private TokensDto tokensDto;

	@BeforeEach
	void setUp() {
		memberSignUpDto = new MemberSignUpDto("test", "test@test", "Test1!@#",
			LocalDate.now(), "01012345678");

		memberDto = MemberDto.builder()
			.nickName("test")
			.email("test@test")
			.birth(LocalDate.now())
			.phone("01012345678")
			.build();

		memberSignInDto = new MemberSignInDto("test@test", "testPass");

		memberWithdrawDto = new MemberWithdrawDto("testPass");

		accessTokenDto = new AccessTokenDto("accessToken");
	}

	@Test
	@DisplayName("회원가입 성공")
		// @WithMockUser
	void success_signUp() throws Exception {
		// Given
		when(memberService.saveMember(any()))
			.thenReturn(memberDto);

		// When
		ResultActions perform = mockMvc.perform(post("/auth/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberSignUpDto)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isCreated(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(201),
				jsonPath("$.message")
					.value(MemberResponseCode.SUCCESS_SIGN_UP.getMessage()),
				jsonPath("$.result.nick_name")
					.value("test"),
				jsonPath("$.result.email")
					.value("test@test"),
				jsonPath("$.result.birth")
					.value(LocalDate.now().toString()),
				jsonPath("$.result.phone")
					.value("01012345678")
			);

	}

	@Test
	@DisplayName("로그인 성공")
	void success_signIn() throws Exception {
		// Given
		when(tokensDto.getAccessToken())
			.thenReturn("accessToken");
		when(tokensDto.getRefreshToken())
			.thenReturn("refreshToken");

		when(memberService.signIn(any(), any()))
			.thenReturn(tokensDto);

		// When
		ResultActions perform = mockMvc.perform(post("/auth/signin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberSignInDto)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value(MemberResponseCode.SUCCESS_SIGN_IN.getMessage()),
				jsonPath("$.result.access_token")
					.value("accessToken"),
				jsonPath("$.result.refresh_token")
					.value("refreshToken")
			);

	}

	@Test
	@DisplayName("록그아웃 성공")
	void success_signOut() throws Exception {
		// Given

		// When
		ResultActions perform = mockMvc.perform(post("/auth/signout"));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value(MemberResponseCode.SUCCESS_SIGN_OUT.getMessage())
			);

	}

	@Test
	@DisplayName("회원탈퇴 성공")
	void success_withdraw() throws Exception {
		// Given
		when(memberService.withdraw(any(), any(), any()))
			.thenReturn(1L);

		// When
		ResultActions perform = mockMvc.perform(delete("/auth/withdraw")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberWithdrawDto)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value(MemberResponseCode.SUCCESS_WITHDRAW.getMessage()),
				jsonPath("$.result")
					.value(1L)
			);

	}

	@Test
	@DisplayName("access token 재발급 성공")
	void success_regenerateAccessToken() throws Exception {
		// Given
		when(memberService.regenerateAccessToken(anyString()))
			.thenReturn(accessTokenDto);

		// When
		ResultActions perform = mockMvc.perform(post("/auth/reissue"));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status")
					.value(200),
				jsonPath("$.message")
					.value(MemberResponseCode.SUCCESS_REGENERATE_ACCESS_TOKEN.getMessage()),
				jsonPath("$.result.access_token")
					.value("accessToken")
			);

	}
}
