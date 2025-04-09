package com.nbc.newsfeeds.domain.friend.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.common.util.CursorPaginationUtil;
import com.nbc.newsfeeds.domain.friend.entity.Friendship;
import com.nbc.newsfeeds.domain.friend.entity.FriendshipStatus;
import com.nbc.newsfeeds.domain.friend.exception.FriendBizException;
import com.nbc.newsfeeds.domain.friend.exception.FriendExceptionCode;
import com.nbc.newsfeeds.domain.friend.model.request.RequestFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RespondToFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendResponse;
import com.nbc.newsfeeds.domain.friend.repository.FriendshipRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class FriendService {

	private final FriendshipRepository friendshipRepository;

	@Transactional
	public void requestFriend(Long memberId, RequestFriendRequest req) {
		validateNotSelfRequest(memberId, req.targetMemberId());

		Friendship friendship = friendshipRepository.findByFriendId(req.targetMemberId())
			.orElse(null);

		if (friendship != null) {
			friendship.reRequest();
			return;
		}

		friendshipRepository.save(Friendship.of(memberId, req.targetMemberId()));
	}

	@Transactional
	public void respondToFriendRequest(Long memberId, Long friendshipId, RespondToFriendRequest req) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);

		validateIsReceiver(memberId, friendship);
		validateRequestIsPending(friendship);

		switch (req.status()) {
			case ACCEPT -> friendship.accept();
			case DECLINE -> friendship.decline();
		}
	}

	@Transactional
	public void deleteFriend(Long memberId, Long friendshipId) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);

		validateIsFriend(memberId, friendship);
		validateRequestIsAccepted(friendship);

		friendship.delete();
	}

	public CursorPageResponse<FriendResponse> findFriends(Long memberId, CursorPageRequest req) {
		PageRequest pageRequest = PageRequest.of(0, req.getSize() + 1);
		List<FriendResponse> friends = friendshipRepository.findFriends(
			memberId, req.getCursor(), pageRequest
		);
		return CursorPaginationUtil.paginate(friends, req.getSize(), FriendResponse::friendshipId);
	}

	public CursorPageResponse<FriendRequestResponse> findReceivedFriendRequests(Long memberId, CursorPageRequest req) {
		PageRequest pageRequest = PageRequest.of(0, req.getSize() + 1);
		List<FriendRequestResponse> friendRequests = friendshipRepository.findReceivedFriendRequests(
			memberId, req.getCursor(), pageRequest
		);
		return CursorPaginationUtil.paginate(friendRequests, req.getSize(), FriendRequestResponse::friendshipId);
	}

	public CursorPageResponse<FriendRequestResponse> findSentFriendRequests(Long memberId, CursorPageRequest req) {
		PageRequest pageRequest = PageRequest.of(0, req.getSize() + 1);
		List<FriendRequestResponse> friendRequests = friendshipRepository.findSentFriendRequests(
			memberId, req.getCursor(), pageRequest
		);
		return CursorPaginationUtil.paginate(friendRequests, req.getSize(), FriendRequestResponse::friendshipId);
	}

	@Transactional
	public void cancelFriendRequest(Long memberId, Long friendshipId) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);

		validateIsRequester(memberId, friendship);
		validateRequestIsPending(friendship);

		friendship.delete();
	}

	private Friendship getFriendshipOrThrow(Long friendshipId) {
		return friendshipRepository.findById(friendshipId)
			.orElseThrow(() -> new FriendBizException(FriendExceptionCode.FRIEND_REQUEST_NOT_FOUND));
	}

	private void validateNotSelfRequest(Long memberId, Long targetMemberId) {
		if (Objects.equals(memberId, targetMemberId)) {
			throw new FriendBizException(FriendExceptionCode.CANNOT_REQUEST_SELF);
		}
	}

	private void validateRequestIsPending(Friendship friendship) {
		if (!Objects.equals(friendship.getStatus(), FriendshipStatus.PENDING)) {
			throw new FriendBizException(FriendExceptionCode.ALREADY_PROCESSED_REQUEST);
		}
	}

	private void validateRequestIsAccepted(Friendship friendship) {
		if (!Objects.equals(friendship.getStatus(), FriendshipStatus.ACCEPTED)) {
			throw new FriendBizException(FriendExceptionCode.NOT_ACCEPTED_REQUEST);
		}
	}

	private void validateIsReceiver(Long memberId, Friendship friendship) {
		if (!Objects.equals(friendship.getFriendId(), memberId)) {
			throw new FriendBizException(FriendExceptionCode.NOT_FRIEND_REQUEST_RECEIVER);
		}
	}

	private void validateIsFriend(Long memberId, Friendship friendship) {
		if (!Objects.equals(friendship.getMemberId(), memberId)
			&& !Objects.equals(friendship.getFriendId(), memberId)
		) {
			throw new FriendBizException(FriendExceptionCode.NOT_FRIEND_PARTICIPANT);
		}
	}

	private void validateIsRequester(Long memberId, Friendship friendship) {
		if (!Objects.equals(friendship.getMemberId(), memberId)) {
			throw new FriendBizException(FriendExceptionCode.NOT_FRIEND_REQUEST_SENDER);
		}
	}
}
