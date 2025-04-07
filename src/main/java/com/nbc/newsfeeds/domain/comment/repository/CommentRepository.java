package com.nbc.newsfeeds.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbc.newsfeeds.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
