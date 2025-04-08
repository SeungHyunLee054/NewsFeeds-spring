package com.nbc.newsfeeds.domain.friend.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.common.exception.BizException;
import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.common.util.CursorPaginationUtil;
import com.nbc.newsfeeds.domain.friend.entity.Friendship;
import com.nbc.newsfeeds.domain.friend.entity.FriendshipStatus;
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

	private final FriendshipRepository friendRepository;

	@Transactional
	public void requestFriend(Long memberId, RequestFriendRequest req) {
		validateNotSelfRequest(memberId, req.targetMemberId());

		Friendship friendship = friendRepository.findByFriendId(req.targetMemberId())
			.orElse(null);

		if (friendship != null) {
			switch (friendship.getStatus()) {
				case PENDING -> throw new BizException(FriendExceptionCode.ALREADY_REQUESTED);
				case ACCEPTED -> throw new BizException(FriendExceptionCode.ALREADY_FRIENDS);
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

		validateIsReceiver(memberId, friendship);
		validateRequestIsPending(friendship);

		friendship.updateStatus(req.status());
		friendRepository.save(friendship);
	}

	@Transactional
	public void deleteFriend(Long memberId, Long friendshipId) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);

		validateIsFriend(memberId, friendship);
		validateRequestIsAccepted(friendship);

		friendship.updateStatus(FriendshipStatus.DELETED);
		friendRepository.save(friendship);
	}

	public CursorPageResponse<FriendResponse> findFriends(Long memberId, CursorPageRequest req) {
		PageRequest pageReq = PageRequest.of(0, req.getSize());
		List<FriendResponse> friends = friendRepository.findFriends(
			memberId, req.getCursor(), req.getSize() + 1, pageReq
		);

		return CursorPaginationUtil.paginate(friends, req.getSize(), FriendResponse::friendshipId);
	}

	public CursorPageResponse<FriendRequestResponse> findReceivedFriendRequests(Long memberId, CursorPageRequest req) {
		PageRequest pageReq = PageRequest.of(0, req.getSize());
		List<FriendRequestResponse> friendRequests = friendRepository.findReceivedFriendRequests(
			memberId, req.getCursor(), req.getSize() + 1, pageReq
		);

		return CursorPaginationUtil.paginate(friendRequests, req.getSize(), FriendRequestResponse::friendshipId);
	}

	public CursorPageResponse<FriendRequestResponse> findSentFriendRequests(Long memberId, CursorPageRequest req) {
		PageRequest pageReq = PageRequest.of(0, req.getSize());
		List<FriendRequestResponse> friendRequests = friendRepository.findSentFriendRequests(
			memberId, req.getCursor(), req.getSize() + 1, pageReq
		);

		return CursorPaginationUtil.paginate(friendRequests, req.getSize(), FriendRequestResponse::friendshipId);
	}

	@Transactional
	public void cancelFriendRequest(Long memberId, Long friendshipId) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);

		validateIsRequester(memberId, friendship);
		validateRequestIsPending(friendship);

		friendship.updateStatus(FriendshipStatus.CANCELLED);
		friendRepository.save(friendship);
	}

	private Friendship getFriendshipOrThrow(Long friendshipId) {
		return friendRepository.findById(friendshipId)
			.orElseThrow(() -> new BizException(FriendExceptionCode.FRIEND_REQUEST_NOT_FOUND));
	}

	private void validateNotSelfRequest(Long memberId, Long targetMemberId) {
		if (Objects.equals(memberId, targetMemberId)) {
			throw new BizException(FriendExceptionCode.CANNOT_REQUEST_SELF);
		}
	}

	private void validateRequestIsPending(Friendship friendship) {
		if (!Objects.equals(friendship.getStatus(), FriendshipStatus.PENDING)) {
			throw new BizException(FriendExceptionCode.ALREADY_PROCESSED_REQUEST);
		}
	}

	private void validateRequestIsAccepted(Friendship friendship) {
		if (!Objects.equals(friendship.getStatus(), FriendshipStatus.ACCEPTED)) {
			throw new BizException(FriendExceptionCode.NOT_ACCEPTED_REQUEST);
		}
	}

	private void validateIsReceiver(Long memberId, Friendship friendship) {
		if (!Objects.equals(friendship.getFriendId(), memberId)) {
			throw new BizException(FriendExceptionCode.NOT_FRIEND_REQUEST_RECEIVER);
		}
	}

	private void validateIsFriend(Long memberId, Friendship friendship) {
		if (!Objects.equals(friendship.getMemberId(), memberId)
			&& !Objects.equals(friendship.getFriendId(), memberId)
		) {
			throw new BizException(FriendExceptionCode.NOT_FRIEND_PARTICIPANT);
		}
	}

	private void validateIsRequester(Long memberId, Friendship friendship) {
		if (!Objects.equals(friendship.getMemberId(), memberId)) {
			throw new BizException(FriendExceptionCode.NOT_FRIEND_REQUEST_SENDER);
		}
	}
}
