package com.nbc.newsfeeds.domain.member.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.dto.request.MemberUpdateDto;
import com.nbc.newsfeeds.domain.member.dto.response.MemberDto;
import com.nbc.newsfeeds.domain.member.service.MemberService;
import com.nbc.newsfeeds.domain.support.security.TestSecurityConfig;

@WebMvcTest(controllers = ProfileController.class)
@Import(TestSecurityConfig.class)
class ProfileControllerTest {
	@MockitoBean
	private MemberService memberService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Spy
	private MemberDto memberDto;

	@Spy
	private MemberUpdateDto memberUpdateDto;

	@BeforeEach
	void setUp() {
		memberDto = MemberDto.builder()
			.nickName("test")
			.email("test@test")
			.birth(LocalDate.now())
			.phone("01012345678")
			.build();

		memberUpdateDto = new MemberUpdateDto(null,
			new MemberUpdateDto.PasswordUpdateForm("testPass", "newTestPass"));

	}

	@Test
	@DisplayName("유저 프로필 조회 성공")
	void success_getMemberProfile() throws Exception {
		// Given
		when(memberService.getMemberProfile(anyLong(), any()))
			.thenReturn(memberDto);

		// When
		ResultActions perform = mockMvc.perform(get("/profiles/{memberId}", 1L));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status_code")
					.value(200),
				jsonPath("$.message")
					.value(MemberResponseCode.SUCCESS_GET_MEMBER_PROFILE.getMessage()),
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
	@DisplayName("유저 수정 성공")
	void success_updateMemberProfile() throws Exception {
		// Given
		when(memberService.updateMemberProfile(any(), any()))
			.thenReturn(memberDto);

		// When
		ResultActions perform = mockMvc.perform(patch("/profiles")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(memberUpdateDto)));

		// Then
		perform.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.success")
					.value(true),
				jsonPath("$.status_code")
					.value(200),
				jsonPath("$.message")
					.value(MemberResponseCode.SUCCESS_UPDATE_MEMBER_PROFILE.getMessage()),
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

}
