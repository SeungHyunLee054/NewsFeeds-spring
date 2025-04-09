package com.nbc.newsfeeds.domain.comment.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.common.response.CommonResponses;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentCreateRequest;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentUpdateRequest;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentCreateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentDetailAndUpdateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentListFindResponse;
import com.nbc.newsfeeds.domain.comment.service.CommentService;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

	private final CommentService commentService;

	@PostMapping()
	public ResponseEntity<CommonResponse<CommentCreateResponse>> createComment(
		@RequestParam @Positive Long feedId,
		@Valid @RequestBody CommentCreateRequest create,
		@AuthenticationPrincipal MemberAuthDto authUser
	) {
		return new ResponseEntity<>(commentService.createComment(feedId, create, authUser), HttpStatus.CREATED);
	}

	@GetMapping()
	public ResponseEntity<CommonResponses<CommentListFindResponse.CommentListItem>> getCommentsByFeedId(
		@RequestParam @Positive Long feedId,
		@PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return new ResponseEntity<>(commentService.getCommentsByFeedId(feedId, pageable), HttpStatus.OK);
	}

	@GetMapping("/{commentId}")
	public ResponseEntity<CommonResponse<CommentDetailAndUpdateResponse>> getCommentById(@PathVariable @Positive Long commentId) {
		return new ResponseEntity<>(commentService.getCommentById(commentId), HttpStatus.OK);
	}

	@PutMapping("/{commentId}")
	public ResponseEntity<CommonResponse<CommentDetailAndUpdateResponse>> updateComment(
		@PathVariable @Positive Long commentId,
		@Valid @RequestBody CommentUpdateRequest request,
		@AuthenticationPrincipal MemberAuthDto authUser
	) {
		return new ResponseEntity<>(commentService.updateComment(commentId, request, authUser), HttpStatus.OK);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<CommonResponse<Long>> deleteComment(
		@PathVariable @Positive Long commentId,
		@AuthenticationPrincipal MemberAuthDto authUser
	) {
		return new ResponseEntity<>(commentService.deleteByCommentId(commentId, authUser), HttpStatus.OK);
	}
}
