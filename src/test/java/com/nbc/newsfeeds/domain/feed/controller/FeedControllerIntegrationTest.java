package com.nbc.newsfeeds.domain.feed.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;
import com.nbc.newsfeeds.domain.support.fixture.FixtureFactory;
import com.nbc.newsfeeds.domain.support.security.TestAuthHelper;
import com.nbc.newsfeeds.domain.support.security.TestSecurityConfig;
import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class FeedControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FeedRepository feedRepository;

	private Long memberId;
	private Member member;

	@BeforeEach
	void setUp() {
		Map<String, Object> fieldValues = new HashMap<>();
		fieldValues.put("id", null);
		fieldValues.put("email", FixtureFactory.generateRandomEmail());
		fieldValues.put("phone", FixtureFactory.generateRandomPhoneNumber());
		fieldValues.put("roles", List.of("ROLE_USER"));
		fieldValues.put("isDeleted", false);

		List<Member> members = FixtureFactory.createFixtures(1, Member.class, fieldValues);
		Member member = memberRepository.save(members.get(0));
		memberId = member.getId();

		this.member = member;
	}

	@Test
	@DisplayName("게시글 등록 성공")
	void createFeed_shouldSucceed() throws Exception {
		FeedRequestDto request = FeedRequestDto.builder()
			.title("테스트 제목")
			.content("테스트 내용")
			.build();

		mockMvc.perform(post("/feeds")
				.with(TestAuthHelper.customAuth(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.result.title").value("테스트 제목"))
			.andExpect(jsonPath("$.result.content").value("테스트 내용"));
	}

	@Test
	@DisplayName("게시글 ID기반 단건 조회 성공")
	void getFeedTest() throws Exception{
		Feed savedFeed = feedRepository.save(Feed.builder()
			.title("단건 테스트 제목")
			.content("단건 테스트 내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(member)
			.build()
		);

		mockMvc.perform(get("/feeds/{id}", savedFeed.getId())
				.with(TestAuthHelper.customAuth(memberId)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.title").value("단건 테스트 제목"))
			.andExpect(jsonPath("$.result.content").value("단건 테스트 내용"));
	}

	@Test
	@DisplayName("전체 게시글 목록 조회 성공")
	void getFeedsTest() throws Exception {

		Feed feed1 = feedRepository.save(Feed.builder()
			.title("첫 번째 게시글")
			.content("내용1")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(memberRepository.findById(memberId).orElseThrow())
			.build());

		Feed feed2 = feedRepository.save(Feed.builder()
			.title("두 번째 게시글")
			.content("내용2")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(memberRepository.findById(memberId).orElseThrow())
			.build());

		mockMvc.perform(get("/feeds")
				.with(TestAuthHelper.customAuth(memberId))
				.param("size", "10")
				.param("cursor", String.valueOf(Long.MAX_VALUE)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.items.length()").value(2))
			.andExpect(jsonPath("$.result.page_info.has_next").value(false));

	}

	@Test
	@DisplayName("게시글 수정 성공")
	void updateFeedTest() throws Exception {
		Feed saveFeed = feedRepository.save(Feed.builder()
			.title("첫 번째 게시글")
			.content("내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(memberRepository.findById(memberId).orElseThrow())
			.build());

		FeedRequestDto updateRequest = FeedRequestDto.builder()
			.title("수정된 제목")
			.content("수정된 내용")
			.build();

		mockMvc.perform(put("/feeds/{id}", saveFeed.getId())
				.with(TestAuthHelper.customAuth(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.title").value("수정된 제목"))
			.andExpect(jsonPath("$.result.content").value("수정된 내용"));
	}

	@Test
	@DisplayName("게시글 삭제 성공")
	void deleteFeedTest() throws Exception {
		Feed saveFeed = feedRepository.save(Feed.builder()
			.title("첫 번째 게시글")
			.content("내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(memberRepository.findById(memberId).orElseThrow())
			.build());

		mockMvc.perform(delete("/feeds/{id}", saveFeed.getId())
				.with(TestAuthHelper.customAuth(memberId)))
			.andExpect(status().isOk());
	}

	//필수값 누락 예외 테스트
	@Test
	@DisplayName("게시글 작성 제목 누락 예외")
	void createFeedWithoutTitle_ShouldThrowValidationException() throws Exception {
		FeedRequestDto request = FeedRequestDto.builder()
			.title("")
			.content("내용")
			.build();

		mockMvc.perform(post("/feeds")
				.with(TestAuthHelper.customAuth(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.result.content[0].message").value("제목은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 작성 내용 누락 예외")
	void createFeedWithoutContent_ShouldThrowValidationException() throws Exception {
		FeedRequestDto request = FeedRequestDto.builder()
			.title("제목")
			.content("")
			.build();

		mockMvc.perform(post("/feeds")
				.with(TestAuthHelper.customAuth(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.result.content[0].field").value("content"))
			.andExpect(jsonPath("$.result.content[0].message").value("내용은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 작성 제목, 내용 누락 예외")
	void createFeedWithoutTitleAndContent_ShouldThrowValidationException() throws Exception {
		FeedRequestDto request = FeedRequestDto.builder()
			.title("")
			.content("")
			.build();

		mockMvc.perform(post("/feeds")
				.with(TestAuthHelper.customAuth(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.result.content.length()").value(2))
			.andExpect(jsonPath("$.result.content[?(@.field=='title')].message").value("제목은 필수입니다."))
			.andExpect(jsonPath("$.result.content[?(@.field=='content')].message").value("내용은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 수정 제목 누락 예외")
	void updateFeedWithoutTitle_ShouldThrowValidationException() throws Exception {
		FeedRequestDto request = FeedRequestDto.builder()
			.title("")
			.content("수정된 내용")
			.build();

		mockMvc.perform(put("/feeds/{id}", 1L)
				.with(TestAuthHelper.customAuth(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.result.content[0].field").value("title"))
			.andExpect(jsonPath("$.result.content[0].message").value("제목은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 수정 내용 누락 예외")
	void updateFeedWithoutContent_ShouldThrowValidationException() throws Exception {
		FeedRequestDto request = FeedRequestDto.builder()
			.title("수정된 제목")
			.content("")
			.build();

		mockMvc.perform(put("/feeds/{id}", 1L)
				.with(TestAuthHelper.customAuth(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.result.content[0].field").value("content"))
			.andExpect(jsonPath("$.result.content[0].message").value("내용은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 수정 제목, 내용 누락 예외")
	void updateFeedWithoutTitleAndContent_ShouldThrowValidationException() throws Exception {
		FeedRequestDto request = FeedRequestDto.builder()
			.title("")
			.content("")
			.build();

		mockMvc.perform(put("/feeds/{id}", 1L)
				.with(TestAuthHelper.customAuth(memberId))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.result.content.length()").value(2))
			.andExpect(jsonPath("$.result.content[?(@.field=='title')].message").value("제목은 필수입니다."))
			.andExpect(jsonPath("$.result.content[?(@.field=='content')].message").value("내용은 필수입니다."));
	}


}
