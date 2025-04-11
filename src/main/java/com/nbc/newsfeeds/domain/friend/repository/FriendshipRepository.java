package com.nbc.newsfeeds.domain.friend.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nbc.newsfeeds.domain.friend.entity.Friendship;
import com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendshipResponse;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

	@Query("""
			SELECT f FROM Friendship f
			WHERE (f.memberId = :memberId AND f.friendId = :targetMemberId) OR (f.memberId = :targetMemberId AND f.friendId = :memberId)
		""")
	List<Friendship> findAllByMemberIdTargetId(Long memberId, Long targetMemberId);

	@Query("""
			SELECT new com.nbc.newsfeeds.domain.friend.model.response.FriendshipResponse(f.id, m.id, m.nickName)
			FROM Friendship f, Member m
			WHERE (f.memberId = :memberId OR f.friendId = :memberId) AND f.status = 'ACCEPTED' AND (f.id < :cursor OR :cursor = 0) AND f.memberId = m.id
			ORDER BY f.id DESC
		""")
	List<FriendshipResponse> findFriends(Long memberId, Long cursor, Pageable pageable);

	@Query("""
			SELECT new com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse(f.id, m.id, m.nickName)
			FROM Friendship f, Member m
			WHERE f.friendId = :memberId AND f.status = 'PENDING' AND (f.id < :cursor OR :cursor = 0) AND f.memberId = m.id
			ORDER BY f.id DESC
		""")
	List<FriendRequestResponse> findReceivedFriendRequests(Long memberId, Long cursor, Pageable pageable);

	@Query("""
			SELECT new com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse(f.id, m.id, m.nickName)
			FROM Friendship f, Member m
			WHERE f.memberId = :memberId AND f.status = 'PENDING' AND (f.id < :cursor OR :cursor = 0) AND f.memberId = m.id
			ORDER BY f.id DESC
		""")
	List<FriendRequestResponse> findSentFriendRequests(Long memberId, Long cursor, Pageable pageable);

	@Query("""
			SELECT f FROM Friendship f
			WHERE f.memberId = :memberId OR f.friendId = :memberId AND f.status = 'ACCEPTED'
		""")
	List<Friendship> findFriendsByMemberId(Long memberId);
}
