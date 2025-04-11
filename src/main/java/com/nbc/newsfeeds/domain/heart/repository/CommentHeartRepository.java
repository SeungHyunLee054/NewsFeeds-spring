package com.nbc.newsfeeds.domain.heart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbc.newsfeeds.domain.heart.entity.CommentHeart;

public interface CommentHeartRepository extends JpaRepository<CommentHeart, Long> {

	boolean existsByMember_IdAndComment_Id(Long memberId, Long commentId);

	void deleteByMember_IdAndComment_Id(Long memberId, Long commentId);
}
