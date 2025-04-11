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
import com.nbc.newsfeeds.domain.friend.model.request.FriendRequestDecision;
import com.nbc.newsfeeds.domain.friend.model.request.RequestFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RespondToFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendshipResponse;
import com.nbc.newsfeeds.domain.friend.repository.FriendCacheRepository;
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
	private final FriendCacheRepository friendCacheRepository;

	/**
	 * 친구 요청을 수행합니다.<br>
	 * 친구가 아니거나 요청이 취소되어 있는 경우 재요청을 수행합니다.
	 * <p>
	 * 자기 자신에게 요청을 한 경우 -> CANNOT_REQUEST_SELF<br>
	 * 존재하지 사용자에게 요청을 한 경우 -> MEMBER_NOT_FOUND<br>
	 * 친구 요청이 존재하는 경우 -> ALREADY_REQUESTED<br>
	 * 이미 친구 상태인 경우 -> ALREADY_FRIENDS
	 *
	 * @param memberId 친구 요청을 보내는 사용자 ID
	 * @param req 친구 요청을 위한 정보
	 * @return 친구 요청 정보
	 * @author 윤정환
	 */
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

		return new FriendshipResponse(friendship.getId(), friend.getId(), friend.getNickName());
	}

	/**
	 * 친구 요청에 대해 수락(ACCPET) 또는 거절(DECLINE) 을 수행합니다.<br>
	 * 친구 요청이 수락되면 친구 목록이 바뀌기 때문에 캐싱된 친구 목록을 날려줍니다.
	 * <p>
	 * 본인이 받은 친구 요청이 아닌 경우 -> NOT_FRIEND_REQUEST_RECEIVER<br>
	 * 친구 요청이 아닌 경우 -> ALREADY_PROCESSED_REQUEST
	 *
	 * @param memberId 응답하는 사용자 ID
	 * @param friendshipId 응답하려는 친구 정보 ID
	 * @param req 친구 요청에 대한 응답
	 * @author 윤정환
	 */
	@Transactional
	public void respondToFriendRequest(Long memberId, Long friendshipId, RespondToFriendRequest req) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);
		friendship.respond(memberId, req.status());

		if (req.status() == FriendRequestDecision.ACCEPT) {
			friendCacheRepository.evictFriends(friendship.getMemberId());
			friendCacheRepository.evictFriends(friendship.getFriendId());
		}
	}

	/**
	 * 친구를 삭제합니다.<br>
	 * 친구가 삭제되면 친구 목록이 바뀌기 때문에 캐싱된 친구 목록을 날려줍니다.
	 * <p>
	 * 본인의 친구 정보가 아닌 경우 -> NOT_FRIEND_PARTICIPANT<br>
	 * 친구 상태가 아닌 경우 -> NOT_ACCEPTED_REQUEST
	 *
	 * @param memberId 친구 삭제를 하려는 사용자 ID
	 * @param friendshipId 삭제하려는 친구 정보 ID
	 * @author 윤정환
	 */
	@Transactional
	public void deleteFriend(Long memberId, Long friendshipId) {
		Friendship friendship = getFriendshipOrThrow(friendshipId);
		friendship.delete(memberId);
		friendCacheRepository.evictFriends(friendship.getMemberId());
		friendCacheRepository.evictFriends(friendship.getFriendId());
	}

	/**
	 * 친구 목록을 조회하여 반환합니다.<br>
	 * 항상 조회 가능한 최대 수만큼 조회를 하여 캐싱을 해둡니다.<br>
	 * 이를 통해 size 에 따라 캐싱을 하는 것이 아닌 size 만큼 slice 하여 사용하는 방법을 사용하고 있습니다.
	 *
	 * @param memberId 조회를 요청한 사용자 ID
	 * @param req 친구 목록 조회를 위한 페이징 정보
	 * @return 페이징된 친구 목록
	 * @author 윤정환
	 */
	public CursorPageResponse<FriendshipResponse> findFriends(Long memberId, CursorPageRequest req) {
		CursorPageResponse<FriendshipResponse> cached = friendCacheRepository.getFriends(memberId, req.getCursor(), req.getSize());
		if (cached != null) {
			return cached;
		}

		List<FriendshipResponse> friends = friendshipRepository.findFriends(
			memberId, req.getCursor(), PageRequest.of(0, 31)
		);

		CursorPageResponse<FriendshipResponse> fullPage
			= CursorPaginationUtil.paginate(friends, req.getSize(), FriendshipResponse::friendshipId);
		friendCacheRepository.saveFriends(memberId, req.getCursor(), fullPage);

		return CursorPaginationUtil.sliceForSize(fullPage, req.getSize());
	}

	/**
	 * 받은 친구 요청 목록을 조회하여 반환합니다.
	 *
	 * @param memberId 조회를 요청한 사용자 ID
	 * @param req 받은 친구 요청 목록 조회를 위한 페이징 정보
	 * @return 페이징된 받은 친구 요청 목록
	 * @author 윤정환
	 */
	public CursorPageResponse<FriendRequestResponse> findReceivedFriendRequests(Long memberId, CursorPageRequest req) {
		PageRequest pageRequest = PageRequest.of(0, req.getSize() + 1);
		List<FriendRequestResponse> friendRequests = friendshipRepository.findReceivedFriendRequests(
			memberId, req.getCursor(), pageRequest
		);
		return CursorPaginationUtil.paginate(friendRequests, req.getSize(), FriendRequestResponse::friendshipId);
	}

	/**
	 * 보낸 친구 요청 목록을 조회하여 반환합니다.
	 *
	 * @param memberId 조회를 요청한 사용자 ID
	 * @param req 보낸 친구 요청 목록 조회를 위한 페이징 정보
	 * @return 페이징된 보낸 친구 요청 목록
	 * @author 윤정환
	 */
	public CursorPageResponse<FriendRequestResponse> findSentFriendRequests(Long memberId, CursorPageRequest req) {
		PageRequest pageRequest = PageRequest.of(0, req.getSize() + 1);
		List<FriendRequestResponse> friendRequests = friendshipRepository.findSentFriendRequests(
			memberId, req.getCursor(), pageRequest
		);
		return CursorPaginationUtil.paginate(friendRequests, req.getSize(), FriendRequestResponse::friendshipId);
	}

	/**
	 * 친구 요청을 취소합니다.
	 * <p>
	 * 존재하지 않는 친구 정보인 경우 -> FRIEND_REQUEST_NOT_FOUND<br>
	 * 자신이 보낸 친구 요청이 아닌 경우 -> NOT_FRIEND_REQUEST_SENDER<br>
	 * 친구 요청 상태가 아닌 경우 -> ALREADY_PROCESSED_REQUEST
	 *
	 * @param memberId 사용자 ID
	 * @param friendshipId 친구 정보 ID
	 * @author 윤정환
	 */
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
