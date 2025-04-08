package com.nbc.newsfeeds.domain.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.feed.entity.Feed;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	Page<Comment> findAllByFeed(Feed feed, Pageable pageable);
}