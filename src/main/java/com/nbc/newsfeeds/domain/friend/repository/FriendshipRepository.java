package com.nbc.newsfeeds.domain.friend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nbc.newsfeeds.domain.friend.entity.Friendship;
import com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendResponse;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

	@Query("""
			SELECT f FROM Friendship f
			WHERE f.memberId = :friendId OR f.friendId = :friendId
		""")
	Optional<Friendship> findByFriendId(Long friendId);

	@Query("""
			SELECT new com.nbc.newsfeeds.domain.friend.model.response.FriendResponse(f.id, m.id, m.nickName)
			FROM Friendship f, Member m
			WHERE (f.memberId = :memberId OR f.friendId = :memberId) AND f.status = 'ACCEPTED' AND (f.id < :cursor OR :cursor = 0) AND f.memberId = m.id
			ORDER BY f.id DESC
		""")
	List<FriendResponse> findFriends(Long memberId, Long cursor, Integer size, Pageable pageable);

	@Query("""
			SELECT new com.nbc.newsfeeds.domain.friend.model.response.FriendRequestResponse(f.id, m.id, m.nickName)
			FROM Friendship f, Member m
			WHERE f.friendId = :memberId AND f.status = 'PENDING' AND (f.id < :cursor OR :cursor = 0) AND f.memberId = m.id
			ORDER BY f.id DESC
		""")
	List<FriendRequestResponse> findFriendRequests(Long memberId, Long cursor, Integer size, Pageable pageable);
}
