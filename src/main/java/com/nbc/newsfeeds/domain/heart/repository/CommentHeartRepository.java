package com.nbc.newsfeeds.domain.heart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nbc.newsfeeds.domain.heart.entity.CommentHeart;

public interface CommentHeartRepository extends JpaRepository<CommentHeart, Long> {

	boolean existsByMember_IdAndComment_Id(Long memberId, Long commentId);

	@Query("""
		SELECT ch
		FROM CommentHeart ch
		JOIN FETCH Comment c
		JOIN FETCH Feed f
		WHERE ch.member.id = :meberId AND c.id = :commentId
		""")
	Optional<CommentHeart> findsByMember_IdAndComment_Id(@Param("memberId")Long memberId, @Param("commentId")Long commentId);

	void deleteByMember_IdAndComment_Id(Long memberId, Long commentId);
}
