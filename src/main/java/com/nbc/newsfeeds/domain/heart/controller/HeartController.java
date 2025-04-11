package com.nbc.newsfeeds.domain.heart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseCode;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;
import com.nbc.newsfeeds.domain.heart.service.CommentHeartService;
import com.nbc.newsfeeds.domain.heart.service.FeedHeartService;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feeds/{feedId}")
@RequiredArgsConstructor
public class HeartController {

	private final FeedHeartService feedHeartService;
	private final CommentHeartService commentHeartService;

	@PostMapping("/likes")
	@Operation(summary = "게시글 좋아요 추가", security = {@SecurityRequirement(name = "bearer-key")})
	public ResponseEntity<CommonResponse<HeartResponseDto>> addFeedHeart(
		@AuthenticationPrincipal MemberAuth memberAuthDto,
		@PathVariable long feedId
	) {
		feedHeartService.addHeart(memberAuthDto.getId(), feedId);
		return ResponseEntity
			.status(HeartResponseCode.HEART_CREATED.getHttpStatus())
			.body(CommonResponse.of(HeartResponseCode.HEART_CREATED, feedHeartService.viewHeart(feedId)));
	}

	@DeleteMapping("/likes")
	@Operation(summary = "게시글 좋아요 취소", security = {@SecurityRequirement(name = "bearer-key")})
	public ResponseEntity<CommonResponse<HeartResponseDto>> cancelFeedHeart(
		@AuthenticationPrincipal MemberAuth memberAuthDto,
		@PathVariable long feedId
	) {
		feedHeartService.cancelHeart(memberAuthDto.getId(), feedId);
		return ResponseEntity
			.status(HeartResponseCode.HEART_REMOVED.getHttpStatus())
			.body(CommonResponse.of(HeartResponseCode.HEART_REMOVED, feedHeartService.viewHeart(feedId)));
	}

	@GetMapping("/likes")
	@Operation(summary = "게시글 좋아요 확인")
	public ResponseEntity<CommonResponse<HeartResponseDto>> viewFeedHeart(
		@PathVariable long feedId
	) {
		return ResponseEntity
			.status(HeartResponseCode.HEART_RETRIEVED.getHttpStatus())
			.body(CommonResponse.of(HeartResponseCode.HEART_RETRIEVED, feedHeartService.viewHeart(feedId)));
	}

	@PostMapping("/comments/{commentId}/likes")
	@Operation(summary = "댓글 좋아요 추가", security = {@SecurityRequirement(name = "bearer-key")})
	public ResponseEntity<CommonResponse<HeartResponseDto>> addCommentHeart(
		@AuthenticationPrincipal MemberAuth memberAuthDto,
		@PathVariable long feedId,
		@PathVariable long commentId
	) {
		commentHeartService.addHeart(memberAuthDto.getId(), feedId, commentId);
		return ResponseEntity
			.status(HeartResponseCode.HEART_CREATED.getHttpStatus())
			.body(CommonResponse.of(HeartResponseCode.HEART_CREATED, commentHeartService.viewHeart(commentId)));
	}

	@DeleteMapping("/comments/{commentId}/likes")
	@Operation(summary = "댓글 좋아요 취소", security = {@SecurityRequirement(name = "bearer-key")})
	public ResponseEntity<CommonResponse<HeartResponseDto>> cancelCommentHeart(
		@AuthenticationPrincipal MemberAuth memberAuthDto,
		@PathVariable long feedId,
		@PathVariable long commentId
	) {
		commentHeartService.cancelHeart(memberAuthDto.getId(), feedId, commentId);
		return ResponseEntity
			.status(HeartResponseCode.HEART_REMOVED.getHttpStatus())
			.body(CommonResponse.of(HeartResponseCode.HEART_REMOVED, commentHeartService.viewHeart(feedId)));
	}

	@GetMapping("/comments/{commentId}/likes")
	@Operation(summary = "댓글 좋아요 확인")
	public ResponseEntity<CommonResponse<HeartResponseDto>> viewCommentHeart(
		@PathVariable long feedId,
		@PathVariable long commentId
	) {
		return ResponseEntity
			.status(HeartResponseCode.HEART_RETRIEVED.getHttpStatus())
			.body(CommonResponse.of(HeartResponseCode.HEART_RETRIEVED, commentHeartService.viewHeart(feedId, commentId)));
	}
}
