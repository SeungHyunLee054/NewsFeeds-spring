package com.nbc.newsfeeds.domain.friend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nbc.newsfeeds.domain.friend.entity.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

	@Query("""
			SELECT f FROM Friendship f
			WHERE (f.memberId = :memberId1 AND f.friendId = :memberId2)
			   OR (f.memberId = :memberId2 AND f.friendId = :memberId1)
			  AND f.status != 'CANCELLED'
		""")
	boolean existsByMemberIdAndFriendId(Long memberId1, Long memberId2);

	@Query("""
			SELECT f FROM Friendship f
			WHERE (f.memberId = :memberId OR f.friendId = :memberId) AND f.status = "ACCEPTED" AND f.id < :cursor
			ORDER BY f.id DESC
			LIMIT :size
		""")
	List<Friendship> findFriendsByIdAndCursor(Long memberId, Long cursor, Integer size);
}
