package com.nbc.newsfeeds.domain.friend.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.domain.friend.entity.Friendship;
import com.nbc.newsfeeds.domain.friend.entity.FriendshipStatus;
import com.nbc.newsfeeds.domain.friend.model.request.CursorPageRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RequestFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RespondToFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.response.CursorPage;
import com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendRequestsResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendsResponse;
import com.nbc.newsfeeds.domain.friend.repository.FriendshipRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class FriendService {

	private final FriendshipRepository friendRepository;

	@Transactional
	public void requestFriend(Long memberId, RequestFriendRequest req) {
		validateNotSelfRequest(memberId, req.targetMemberId());

		Friendship friendship = friendRepository.findByFriendId(req.targetMemberId())
			.orElse(null);

		if (friendship != null) {
			switch (friendship.getStatus()) {
				case PENDING -> throw new RuntimeException("이미 신청이 되어있습니다."); // todo 409 CONFLICT
				case ACCEPTED -> throw new RuntimeException("이미 친구입니다."); // todo 409 CONFLICT
				case DECLINED, CANCELLED -> {
					friendship.updateStatus(FriendshipStatus.PENDING);
					return;
				}
			}
		}

		friendRepository.save(Friendship.of(memberId, req.targetMemberId(), FriendshipStatus.PENDING));
	}

	@Transactional
	public void respondToFriendRequest(Long memberId, Long friendshipId, RespondToFriendRequest req) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);

		validateRequestOwnership(memberId, friendship);
		validateRequestIsPending(friendship);

		friendship.updateStatus(req.status());
		friendRepository.save(friendship);
	}

	@Transactional
	public void deleteFriend(Long memberId, Long friendshipId) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);

		validateRequestOwnership(memberId, friendship);
		validateRequestIsAccepted(friendship);

		friendship.updateStatus(FriendshipStatus.DELETED);
		friendRepository.save(friendship);
	}

	public FriendsResponse findFriends(Long memberId, CursorPageRequest req) {
		PageRequest pageReq = PageRequest.of(0, req.getSize());
		List<FriendResponse> friends = friendRepository.findFriends(
			memberId, req.getCursor(), req.getSize() + 1, pageReq
		);

		boolean hasNext = friends.size() > req.getSize();
		if (hasNext) {
			friends = friends.subList(0, req.getSize());
		}
		Long nextCursor = null;
		if (!friends.isEmpty()) {
			nextCursor = friends.get(friends.size() - 1).friendshipId();
		}

		CursorPage pageInfo = new CursorPage(nextCursor, hasNext);
		return new FriendsResponse(friends, pageInfo);
	}

	public FriendRequestsResponse findFriendRequests(Long memberId, CursorPageRequest req) {
		PageRequest pageReq = PageRequest.of(0, req.getSize());
		List<FriendRequestResponse> friendRequests = friendRepository.findFriendRequests(
			memberId, req.getCursor(), req.getSize() + 1, pageReq
		);

		boolean hasNext = friendRequests.size() > req.getSize();
		if (hasNext) {
			friendRequests = friendRequests.subList(0, req.getSize());
		}
		Long nextCursor = null;
		if (!friendRequests.isEmpty()) {
			nextCursor = friendRequests.get(friendRequests.size() - 1).friendshipId();
		}

		CursorPage pageInfo = new CursorPage(nextCursor, hasNext);
		return new FriendRequestsResponse(friendRequests, pageInfo);
	}

	@Transactional
	public void cancelFriendRequest(Long memberId, Long friendshipId) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);

		validateRequestOwnership(memberId, friendship);
		validateRequestIsPending(friendship);

		friendship.updateStatus(FriendshipStatus.CANCELLED);
		friendRepository.save(friendship);
	}

	private Friendship getFriendshipOrThrow(Long friendshipId) {
		// todo 404 NOT_FOUND
		return friendRepository.findById(friendshipId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 친구 요청입니다."));
	}

	private void validateNotSelfRequest(Long memberId, Long targetMemberId) {
		if (Objects.equals(memberId, targetMemberId)) {
			// todo 400 BAD_REQUEST
			throw new RuntimeException("자기 자신과 친구가 될 수 없습니다.");
		}
	}

	private void validateRequestOwnership(Long memberId, Friendship friendship) {
		if (!Objects.equals(friendship.getMemberId(), memberId)) {
			// todo 403 FORBIDDEN
			throw new RuntimeException("본인의 친구 정보가 아닙니다.");
		}
	}

	private void validateRequestIsPending(Friendship friendship) {
		if (!Objects.equals(friendship.getStatus(), FriendshipStatus.PENDING)) {
			// todo 409 CONFLICT
			throw new RuntimeException("이미 처리된 친구 정보가 입니다.");
		}
	}

	private void validateRequestIsAccepted(Friendship friendship) {
		if (!Objects.equals(friendship.getStatus(), FriendshipStatus.ACCEPTED)) {
			// todo 409 CONFLICT
			throw new RuntimeException("본인의 친구가 아닙니다.");
		}
	}

}
