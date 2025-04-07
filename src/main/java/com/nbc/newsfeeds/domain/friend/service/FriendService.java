package com.nbc.newsfeeds.domain.friend.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.domain.friend.constant.FriendshipStatus;
import com.nbc.newsfeeds.domain.friend.entity.Friendship;
import com.nbc.newsfeeds.domain.friend.model.request.CursorPageRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RequestFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RespondToFriendRequest;
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
		if (Objects.equals(memberId, req.targetMemberId())) {
			// todo 400 BAD_REQUEST
			throw new RuntimeException("자기 자신과 친구가 될 수 없습니다.");
		}

		if (friendRepository.existsByMemberIdAndFriendId(memberId, req.targetMemberId())) {
			// todo 409 CONFLICT
			throw new RuntimeException("이미 신청이 되어있거나 친구인 상태입니다.");
		}

		Friendship friendship = Friendship.builder()
			.memberId(memberId)
			.friendId(req.targetMemberId())
			.status(FriendshipStatus.PENDING)
			.build();
		friendRepository.save(friendship);
	}

	@Transactional
	public void respondToFriendRequest(Long memberId, Long friendshipId, RespondToFriendRequest req) {
		// todo 404 NOT_FOUND
		Friendship friendship = friendRepository.findById(friendshipId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 친구 요청입니다."));

		if (!Objects.equals(friendship.getMemberId(), memberId)) {
			// todo 403 FORBIDDEN
			throw new RuntimeException("본인의 친구 요청이 아닙니다.");
		}

		if (!Objects.equals(friendship.getStatus(), FriendshipStatus.PENDING)) {
			// todo 409 CONFLICT
			throw new RuntimeException("이미 처리된 친구 요청입니다.");
		}

		friendship.updateStatus(req.status());
		friendRepository.save(friendship);
	}

	@Transactional
	public void deleteFriend(Long memberId, Long friendshipId) {
		// todo 404 NOT_FOUND
		Friendship friendship = friendRepository.findById(friendshipId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 친구 요청입니다."));

		if (!Objects.equals(friendship.getMemberId(), memberId)
			&& !Objects.equals(friendship.getFriendId(), memberId)
		) {
			// todo 403 FORBIDDEN
			throw new RuntimeException("본인의 친구가 아닙니다.");
		}

		if (!Objects.equals(friendship.getStatus(), FriendshipStatus.ACCEPTED)) {
			// todo 409 CONFLICT
			throw new RuntimeException("본인의 친구가 아닙니다.");
		}

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

		return new FriendsResponse(friends, nextCursor, hasNext);
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

		return new FriendRequestsResponse(friendRequests, nextCursor, hasNext);
	}

	@Transactional
	public void cancelFriendRequest(Long memberId, Long friendshipId) {
		// todo 404 NOT_FOUND
		Friendship friendship = friendRepository.findById(friendshipId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 친구 요청입니다."));

		if (!Objects.equals(friendship.getMemberId(), memberId)) {
			// todo 403 FORBIDDEN
			throw new RuntimeException("본인의 친구 요청이 아닙니다.");
		}

		if (!Objects.equals(friendship.getStatus(), FriendshipStatus.PENDING)) {
			// todo 409 CONFLICT
			throw new RuntimeException("이미 처리된 친구 요청입니다.");
		}

		friendship.updateStatus(FriendshipStatus.CANCELLED);
		friendRepository.save(friendship);
	}
}
