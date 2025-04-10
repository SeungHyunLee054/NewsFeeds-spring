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
import com.nbc.newsfeeds.domain.friend.exception.FriendBizException;
import com.nbc.newsfeeds.domain.friend.exception.FriendExceptionCode;
import com.nbc.newsfeeds.domain.friend.model.request.RequestFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RespondToFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendshipResponse;
import com.nbc.newsfeeds.domain.friend.repository.FriendshipRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class FriendService {

	private final FriendshipRepository friendshipRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public FriendshipResponse requestFriend(Long memberId, RequestFriendRequest req) {
		validateNotSelfRequest(memberId, req.targetMemberId());

		Member friend = getMemberOrThrow(req.targetMemberId());
		Friendship friendship = friendshipRepository.findByFriendId(req.targetMemberId())
			.map(existing -> {
				existing.reRequest();
				return existing;
			})
			.orElseGet(() -> {
				Friendship newFriendship = Friendship.of(memberId, req.targetMemberId());
				return friendshipRepository.save(newFriendship);
			});

		return new  FriendshipResponse(friendship.getId(), friend.getId(), friend.getNickName());
	}

	@Transactional
	public void respondToFriendRequest(Long memberId, Long friendshipId, RespondToFriendRequest req) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);
		friendship.respond(memberId, req.status());
	}

	@Transactional
	public void deleteFriend(Long memberId, Long friendshipId) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);
		friendship.delete(memberId);
	}

	public CursorPageResponse<FriendshipResponse> findFriends(Long memberId, CursorPageRequest req) {
		PageRequest pageRequest = PageRequest.of(0, req.getSize() + 1);
		List<FriendshipResponse> friends = friendshipRepository.findFriends(
			memberId, req.getCursor(), pageRequest
		);
		return CursorPaginationUtil.paginate(friends, req.getSize(), FriendshipResponse::friendshipId);
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
		friendship.cancel(memberId);
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

	private Member getMemberOrThrow(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new FriendBizException(FriendExceptionCode.MEMBER_NOT_FOUND));
	}
}
