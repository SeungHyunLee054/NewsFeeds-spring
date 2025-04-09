package com.nbc.newsfeeds.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbc.newsfeeds.domain.comment.entity.Comment;

public interface CommentCountRepository extends JpaRepository<Comment, Long> {
	int countByFeed_id(Long feedId);
}