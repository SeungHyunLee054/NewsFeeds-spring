package com.nbc.newsfeeds.domain.friend.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nbc.newsfeeds.domain.friend.entity.Friendship;
import com.nbc.newsfeeds.domain.friend.model.response.FriendResponse;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

	@Query("""
			SELECT f FROM Friendship f
			WHERE (f.memberId = :memberId1 AND f.friendId = :memberId2)
			   OR (f.memberId = :memberId2 AND f.friendId = :memberId1)
			  AND f.status != 'CANCELLED'
		""")
	boolean existsByMemberIdAndFriendId(Long memberId1, Long memberId2);

	@Query("""
			SELECT new com.nbc.newsfeeds.domain.friend.model.response.FriendResponse(f.id, m.id, m.name)
			FROM Friendship f, Member m
			WHERE (f.memberId = :memberId OR f.friendId = :memberId) AND f.status = 'ACCEPTED' AND f.id < :cursor AND f.memberId = m.id
			ORDER BY f.id DESC
		""")
	List<FriendResponse> findFriendsByIdAndCursor(Long memberId, Long cursor, Integer size, Pageable pageable);
}
