package com.nbc.newsfeeds.domain.friend.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.nbc.newsfeeds.domain.friend.exception.FriendBizException;
import com.nbc.newsfeeds.domain.friend.exception.FriendExceptionCode;
import com.nbc.newsfeeds.domain.friend.model.request.FriendRequestDecision;

class FriendshipUnitTest {

	private static final Long SENDER_ID = 1L;
	private static final Long RECEIVER_ID = 2L;

	@Test
	@DisplayName("Friendship 엔티티 생성 시 기본 상태는 PENDING 이다")
	void createFriendship_shouldHavePendingStatus() {
		Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);

		assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.PENDING);
		assertThat(friendship.getMemberId()).isEqualTo(SENDER_ID);
		assertThat(friendship.getFriendId()).isEqualTo(RECEIVER_ID);
	}

	@Nested
	@DisplayName("친구 요청 응답")
	class Respond {
		@Test
		@DisplayName("친구 요청 수락 시 상태는 ACCEPTED 가 된다")
		void accept_shouldChangeStatusToAccepted() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);

			friendship.respond(RECEIVER_ID, FriendRequestDecision.ACCEPT);

			assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.ACCEPTED);
		}

		@Test
		@DisplayName("친구 요청 거절 시 상태는 DECLINED 가 된다")
		void decline_shouldChangeStatusToDeclined() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);

			friendship.respond(RECEIVER_ID, FriendRequestDecision.DECLINE);

			assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.DECLINED);
		}

		@Test
		@DisplayName("친구 요청 응답 시 요청자가 아닌 사람이 응답하면 예외가 발생한다")
		void respondByWrongReceiver_ShouldThrowException() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);

			assertThatThrownBy(() -> friendship.respond(3L, FriendRequestDecision.ACCEPT))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.NOT_FRIEND_REQUEST_RECEIVER);
		}

		@Test
		@DisplayName("이미 처리된 친구 요청에 응답하면 예외가 발생한다")
		void respondAlreadyProcessedRequest_ShouldThrowException() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);
			friendship.respond(RECEIVER_ID, FriendRequestDecision.ACCEPT);

			assertThatThrownBy(() -> friendship.respond(RECEIVER_ID, FriendRequestDecision.DECLINE))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.ALREADY_PROCESSED_REQUEST);
		}
	}

	@Nested
	@DisplayName("친구 삭제")
	class Delete {
		@Test
		@DisplayName("친구 요청 수락 후 요청자도 삭제할 수 있다")
		void deleteBySenderAfterAccept_shouldSucceed() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);
			friendship.respond(RECEIVER_ID, FriendRequestDecision.ACCEPT);

			friendship.delete(SENDER_ID);

			assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.DELETED);
		}

		@Test
		@DisplayName("친구 요청 수락 후 수신자도 삭제할 수 있다")
		void deleteByReceiverAfterAccept_shouldSucceed() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);
			friendship.respond(RECEIVER_ID, FriendRequestDecision.ACCEPT);

			friendship.delete(RECEIVER_ID);

			assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.DELETED);
		}

		@Test
		@DisplayName("친구 삭제 시 당사자가 아니면 예외가 발생한다")
		void deleteByNonParticipant_ShouldThrowException() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);
			friendship.respond(RECEIVER_ID, FriendRequestDecision.ACCEPT);

			assertThatThrownBy(() -> friendship.delete(3L))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.NOT_FRIEND_PARTICIPANT);
		}

		@Test
		@DisplayName("수락되지 않은 친구 요청을 삭제하려고 하면 예외가 발생한다")
		void deletePendingRequest_ShouldThrowException() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);

			assertThatThrownBy(() -> friendship.delete(SENDER_ID))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.NOT_ACCEPTED_REQUEST);
		}
	}

	@Nested
	@DisplayName("친구 요청 취소")
	class Cancel {
		@Test
		@DisplayName("요청자가 친구 요청을 취소하면 상태는 CANCELLED 가 된다")
		void cancelRequest_shouldChangeStatusToCancelled() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);

			friendship.cancel(SENDER_ID);

			assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.CANCELLED);
		}

		@Test
		@DisplayName("요청자가 아닌 사람이 친구 요청을 취소하면 예외가 발생한다")
		void cancelByNotSender_ShouldThrowException() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);

			assertThatThrownBy(() -> friendship.cancel(RECEIVER_ID))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.NOT_FRIEND_REQUEST_SENDER);
		}

		@Test
		@DisplayName("이미 응답된 요청을 취소하면 예외가 발생한다")
		void cancelAfterResponded_ShouldThrowException() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);
			friendship.respond(RECEIVER_ID, FriendRequestDecision.DECLINE);

			assertThatThrownBy(() -> friendship.cancel(SENDER_ID))
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.ALREADY_PROCESSED_REQUEST);
		}
	}

	@Nested
	@DisplayName("친구 재요청")
	class ReRequest {

		@Test
		@DisplayName("친구 요청이 취소된 상태에서 재요청하면 상태는 PENDING 이 된다")
		void reRequest_WhenCancelled_shouldSucceed() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);
			friendship.cancel(SENDER_ID);

			friendship.reRequest();

			assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.PENDING);
		}

		@Test
		@DisplayName("친구 요청이 거절된 상태에서 재요청하면 상태는 PENDING 이 된다")
		void reRequest_WhenDeclined_shouldSucceed() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);
			friendship.respond(RECEIVER_ID, FriendRequestDecision.DECLINE);

			friendship.reRequest();

			assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.PENDING);
		}

		@Test
		@DisplayName("친구 요청이 수락된 상태에서 재요청 시 예외가 발생한다")
		void reRequest_WhenAccepted_ShouldThrowException() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);
			friendship.respond(RECEIVER_ID, FriendRequestDecision.ACCEPT);

			assertThatThrownBy(friendship::reRequest)
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.ALREADY_FRIENDS);
		}

		@Test
		@DisplayName("친구 요청이 이미 존재할 경우 재요청 시 예외가 발생한다")
		void reRequest_WhenPending_ShouldThrowException() {
			Friendship friendship = Friendship.of(SENDER_ID, RECEIVER_ID);

			assertThatThrownBy(friendship::reRequest)
				.isInstanceOf(FriendBizException.class)
				.extracting("responseCode")
				.isEqualTo(FriendExceptionCode.ALREADY_REQUESTED);
		}
	}
}
