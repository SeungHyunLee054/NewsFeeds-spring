package com.nbc.newsfeeds.domain.friend.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.domain.friend.entity.Friendship;
import com.nbc.newsfeeds.domain.friend.exception.FriendBizException;
import com.nbc.newsfeeds.domain.friend.exception.FriendExceptionCode;
import com.nbc.newsfeeds.domain.friend.model.request.FriendRequestDecision;
import com.nbc.newsfeeds.domain.friend.model.request.RequestFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RespondToFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendResponse;
import com.nbc.newsfeeds.domain.friend.repository.FriendshipRepository;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class FriendServiceUnitTest {

	@Mock
	private FriendshipRepository friendshipRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private FriendService friendService;

	private final Long MEMBER_ID = 1L;
	private final Long FRIEND_ID = 2L;

	@Nested
	@DisplayName("requestFriend 메서드")
	class RequestFriend {

		@Test
		@DisplayName("친구가 존재하지 않으면 새로운 요청 생성")
		void sendRequest_shouldSucceed() {
			RequestFriendRequest req = new RequestFriendRequest(FRIEND_ID);
			given(friendshipRepository.findByFriendId(FRIEND_ID)).willReturn(Optional.empty());
			given(memberRepository.existsById(FRIEND_ID)).willReturn(true);

			friendService.requestFriend(MEMBER_ID, req);

			verify(friendshipRepository).save(any(Friendship.class));
		}

		@Test
		@DisplayName("자기 자신에게 친구 요청 시 예외 발생")
		void sendRequest_toSelf_shouldThrowException() {
			RequestFriendRequest req = new RequestFriendRequest(MEMBER_ID);

			assertThatThrownBy(() -> friendService.requestFriend(MEMBER_ID, req))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.CANNOT_REQUEST_SELF);
		}

		@Test
		@DisplayName("존재하지 않는 사용자에게 친구 요청 시 예외 발생")
		void sendRequest_whenMemberNotFound_shouldThrowException() {
			RequestFriendRequest req = new RequestFriendRequest(3L);
			given(memberRepository.existsById(3L)).willReturn(false);

			assertThatThrownBy(() -> friendService.requestFriend(MEMBER_ID, req))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.MEMBER_NOT_FOUND);
		}

		@Test
		@DisplayName("이미 요청된 친구라면 재요청 시 reRequest 호출됨")
		void sendRequest_whenAlreadyRequested_shouldCallReRequest() {
			RequestFriendRequest req = new RequestFriendRequest(FRIEND_ID);
			Friendship friendship = mock(Friendship.class);
			given(friendshipRepository.findByFriendId(FRIEND_ID)).willReturn(Optional.of(friendship));
			given(memberRepository.existsById(FRIEND_ID)).willReturn(true);

			friendService.requestFriend(MEMBER_ID, req);

			verify(friendship).reRequest();
		}
	}

	@Nested
	@DisplayName("respondToFriendRequest 메서드")
	class RespondFriendRequest {

		@Test
		@DisplayName("정상적인 친구 요청 시 respond 호출")
		void respondToRequest_shouldSucceed() {
			Friendship friendship = mock(Friendship.class);
			RespondToFriendRequest req = new RespondToFriendRequest(FriendRequestDecision.ACCEPT);
			given(friendshipRepository.findById(1L)).willReturn(Optional.of(friendship));

			friendService.respondToFriendRequest(MEMBER_ID, 1L, req);

			verify(friendship).respond(MEMBER_ID, req.status());
		}

		@Test
		@DisplayName("요청이 존재하지 않으면 예외 발생")
		void respondToRequest_whenRequestNotFound_shouldThrowException() {
			given(friendshipRepository.findById(1L)).willReturn(Optional.empty());
			RespondToFriendRequest req = new RespondToFriendRequest(FriendRequestDecision.ACCEPT);

			assertThatThrownBy(() -> friendService.respondToFriendRequest(MEMBER_ID, 1L, req))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.FRIEND_REQUEST_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("deleteFriend 메서드")
	class DeleteFriend {

		@Test
		@DisplayName("정상적인 삭제 요청 시 delete 호출됨")
		void deleteFriend_shouldSucceed() {
			Friendship friendship = mock(Friendship.class);
			given(friendshipRepository.findById(1L)).willReturn(Optional.of(friendship));

			friendService.deleteFriend(MEMBER_ID, 1L);

			verify(friendship).delete(MEMBER_ID);
		}

		@Test
		@DisplayName("친구가 아닐 때 삭제 요청시 예외 발생")
		void deleteFriend_whenNotFriend_shouldThrowException() {
			given(friendshipRepository.findById(10L)).willReturn(Optional.empty());

			assertThatThrownBy(() -> friendService.deleteFriend(MEMBER_ID, 10L))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.FRIEND_REQUEST_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("cancelFriendRequest 메서드")
	class CancelFriendRequest {

		@Test
		@DisplayName("정삭적인 취소 요청 시 cancel 호출됨")
		void cancelRequest_shouldSucceed() {
			Friendship friendship = mock(Friendship.class);
			given(friendshipRepository.findById(1L)).willReturn(Optional.of(friendship));

			friendService.cancelFriendRequest(MEMBER_ID, 1L);

			verify(friendship).cancel(MEMBER_ID);
		}

		@Test
		@DisplayName("존재하지 않는 요청을 취소하면 예외")
		void cancelRequest_whenRequestNotFound_shouldThrowException() {
			given(friendshipRepository.findById(1L)).willReturn(Optional.empty());

			assertThatThrownBy(() -> friendService.cancelFriendRequest(MEMBER_ID, 1L))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.FRIEND_REQUEST_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("조회 관련 메서드")
	class Retrieval {

		@Test
		@DisplayName("친구 목록 조회")
		void findFriends_shouldSucceed() {
			CursorPageRequest req = new CursorPageRequest(null, 10);
			List<FriendResponse> result = List.of(new FriendResponse(1L, 1L, "name"));
			given(friendshipRepository.findFriends(MEMBER_ID, null, PageRequest.of(0, 11)))
				.willReturn(result);

			CursorPageResponse<FriendResponse> res = friendService.findFriends(MEMBER_ID, req);

			assertThat(res.items()).hasSize(1);
		}

		@Test
		@DisplayName("받은 요청 목록 조회")
		void findReceivedFriendRequests_shouldSucceed() {
			CursorPageRequest req = new CursorPageRequest(null, 10);
			List<FriendRequestResponse> result = List.of(new FriendRequestResponse(1L, 1L, "name"));
			given(friendshipRepository.findReceivedFriendRequests(MEMBER_ID, null, PageRequest.of(0, 11)))
				.willReturn(result);

			CursorPageResponse<FriendRequestResponse> res = friendService.findReceivedFriendRequests(MEMBER_ID, req);

			assertThat(res.items()).hasSize(1);
		}

		@Test
		@DisplayName("보낸 요청 목록 조회")
		void findSentFriendRequests_shouldSucceed() {
			CursorPageRequest req = new CursorPageRequest(null, 10);
			List<FriendRequestResponse> result = List.of(new FriendRequestResponse(1L, 1L, "name"));
			given(friendshipRepository.findSentFriendRequests(MEMBER_ID, null, PageRequest.of(0, 11)))
				.willReturn(result);

			CursorPageResponse<FriendRequestResponse> res = friendService.findSentFriendRequests(MEMBER_ID, req);

			assertThat(res.items()).hasSize(1);
		}
	}
}