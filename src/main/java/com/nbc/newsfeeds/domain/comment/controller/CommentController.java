package com.nbc.newsfeeds.domain.comment.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.domain.comment.dto.CommentCreateResponse;
import com.nbc.newsfeeds.domain.comment.dto.CommentCreateRequest;
import com.nbc.newsfeeds.domain.comment.dto.CommentResponse;
import com.nbc.newsfeeds.domain.comment.dto.CommentUpdateRequest;
import com.nbc.newsfeeds.domain.comment.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping()
	public ResponseEntity<CommentResponse> createComment(
		@RequestParam Long feedId,
		@RequestBody CommentCreateRequest create
	) {
		return new ResponseEntity<>(commentService.createComment(feedId, create), HttpStatus.CREATED);
	}

	@GetMapping()
	public ResponseEntity<CommentResponse> getCommentsByFeedId(
		@RequestParam Long feedId,
		@PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return new ResponseEntity<>(commentService.getCommentsByFeedId(feedId, pageable), HttpStatus.OK);
	}

	@GetMapping("/{commentId}")
	public ResponseEntity<CommentResponse> getCommentByFeedId(@PathVariable Long commentId) {
		return new ResponseEntity<>(commentService.getCommentById(commentId), HttpStatus.OK);
	}

	@PutMapping("/{commentId}")
	public ResponseEntity<CommentResponse> updateComment(
		@PathVariable Long commentId,
		@RequestBody CommentUpdateRequest request
	) {
		return new ResponseEntity<>(commentService.updateComment(commentId, request), HttpStatus.OK);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<CommentResponse> deleteComment(@PathVariable Long commentId) {
		return new ResponseEntity<>(commentService.deleteByCommentId(commentId), HttpStatus.OK);
	}
}
