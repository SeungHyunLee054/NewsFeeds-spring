package com.nbc.newsfeeds.domain.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.domain.comment.dto.CommentRequestCreate;
import com.nbc.newsfeeds.domain.comment.dto.CommentResponse;
import com.nbc.newsfeeds.domain.comment.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping()
	public ResponseEntity<Void> createComment(
		@RequestParam Long feedId,
		@RequestBody CommentRequestCreate create
	) {

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
