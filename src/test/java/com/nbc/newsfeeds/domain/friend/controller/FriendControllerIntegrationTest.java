package com.nbc.newsfeeds.domain.friend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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
import com.nbc.newsfeeds.domain.friend.entity.Friendship;
import com.nbc.newsfeeds.domain.friend.model.request.FriendRequestDecision;
import com.nbc.newsfeeds.domain.friend.model.request.RequestFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RespondToFriendRequest;
import com.nbc.newsfeeds.domain.friend.repository.FriendshipRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;
import com.nbc.newsfeeds.domain.support.fixture.TestMemberFactory;
import com.nbc.newsfeeds.domain.support.security.TestAuthHelper;
import com.nbc.newsfeeds.domain.support.security.TestSecurityConfig;

@Import(TestSecurityConfig.class)
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class FriendControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FriendshipRepository friendshipRepository;

	private Long memberId;
	private Long friendId;

	@BeforeEach
	void setUp() {
		List<Member> members = TestMemberFactory.createDefaultMembers(2);
		memberRepository.saveAll(members);

		memberId = members.get(0).getId();
		friendId = members.get(1).getId();
	}

	@Test
	@DisplayName("친구 요청 성공")
	void requestFriend_shouldSucceed() throws Exception {
		RequestFriendRequest req = new RequestFriendRequest(friendId);

		mockMvc.perform(post("/friends/requests")
			.with(TestAuthHelper.customAuth(memberId))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(req))
		).andExpect(status().isCreated());
	}

	@Test
	@DisplayName("친구 요청 응답 성공")
	void respondToFriendRequest_shouldSucceed() throws Exception {
		Friendship friendship = Friendship.of(memberId, friendId);
		friendshipRepository.save(friendship);

		RespondToFriendRequest req = new RespondToFriendRequest(FriendRequestDecision.ACCEPT);

		mockMvc.perform(patch("/friends/requests/{id}", friendship.getId())
			.with(TestAuthHelper.customAuth(friendId))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(req))
		).andExpect(status().isOk());
	}

	@Test
	@DisplayName("친구 삭제 성공")
	void deleteFriend_shouldSucceed() throws Exception {
		Friendship friendship = Friendship.of(memberId, friendId);
		friendship.respond(friendId, FriendRequestDecision.ACCEPT);
		friendshipRepository.save(friendship);

		mockMvc.perform(delete("/friends/{id}", friendship.getId())
				.with(TestAuthHelper.customAuth(memberId))
			).andExpect(status().isOk());
	}

	@Test
	@DisplayName("친구 요청 취소 성공")
	void cancelRequest_shouldSucceed() throws Exception {
		Friendship friendship = Friendship.of(memberId, friendId);
		friendshipRepository.save(friendship);

		mockMvc.perform(delete("/friends/requests/{id}", friendship.getId())
				.with(TestAuthHelper.customAuth(memberId))
			).andExpect(status().isOk());
	}

	@Test
	@DisplayName("친구 목록 조회 성공")
	void findFriends_shouldSucceed() throws Exception {
		Friendship friendship = Friendship.of(memberId, friendId);
		friendship.respond(friendId, FriendRequestDecision.ACCEPT);
		friendshipRepository.save(friendship);

		mockMvc.perform(get("/friends")
				.with(TestAuthHelper.customAuth(memberId))
				.param("size", "10")
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.result.items.length()").value(1))
			.andExpect(jsonPath("$.result.page_info.next_cursor").value(friendship.getId()))
			.andExpect(jsonPath("$.result.page_info.has_next").value(false));
	}

	@Test
	@DisplayName("받은 친구 요청 조회 성공")
	void findReceivedRequests_shouldSucceed() throws Exception {
		Friendship friendship = Friendship.of(memberId, friendId);
		friendshipRepository.save(friendship);

		mockMvc.perform(get("/friends/requests/received")
				.with(TestAuthHelper.customAuth(friendId))
				.param("size", "10")
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.result.items.length()").value(1))
			.andExpect(jsonPath("$.result.page_info.next_cursor").value(friendship.getId()))
			.andExpect(jsonPath("$.result.page_info.has_next").value(false));
	}

	@Test
	@DisplayName("보낸 친구 요청 조회 성공")
	void findSentRequests_shouldSucceed() throws Exception {
		Friendship friendship = Friendship.of(memberId, friendId);
		friendshipRepository.save(friendship);

		mockMvc.perform(get("/friends/requests/sent")
				.with(TestAuthHelper.customAuth(memberId))
				.param("size", "10")
			).andExpect(status().isOk())
			.andExpect(jsonPath("$.result.items.length()").value(1))
			.andExpect(jsonPath("$.result.page_info.next_cursor").value(friendship.getId()))
			.andExpect(jsonPath("$.result.page_info.has_next").value(false));
	}

}
