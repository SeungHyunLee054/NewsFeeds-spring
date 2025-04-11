package com.nbc.newsfeeds.domain.comment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nbc.newsfeeds.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@EntityGraph(attributePaths = {"feed"})
	Page<Comment> findAllByFeedId(Long feedId, Pageable pageable);

	@EntityGraph(attributePaths = {"feed"})
	Optional<Comment> findWithFeedById(Long commentId);
}
