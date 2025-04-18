package com.nbc.newsfeeds.domain.comment.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.nbc.newsfeeds.common.model.response.CommonResponse;
import com.nbc.newsfeeds.common.model.response.CommonResponses;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentCreateRequest;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentUpdateRequest;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentCreateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentDetailAndUpdateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentListFindResponse;
import com.nbc.newsfeeds.domain.comment.service.CommentService;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

	private final CommentService commentService;

	/**
	 * 댓글 생성
	 *
	 * @author 박형우
	 * @param feedId 댓글 생성할 게시글 id
	 * @param create 생성할 댓글 내용
	 * @param authUser 로그인한 사용자 정보
	 * @return 생성된 댓글 정보
	 */
	@Operation(summary = "댓글 생성", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping
	public ResponseEntity<CommonResponse<CommentCreateResponse>> createComment(
		@RequestParam("feedId") @Positive Long feedId,
		@Valid @RequestBody CommentCreateRequest create,
		@AuthenticationPrincipal MemberAuth authUser
	) {
		return new ResponseEntity<>(commentService.createComment(feedId, create, authUser), HttpStatus.CREATED);
	}

	/**
	 * 게시글 번호로 댓글 조회
	 *
	 * @author 박형우
	 * @param feedId 게시글 id
	 * @param size  조회할 갯수
	 * @param page	현재 페이지
	 * @return 조회된 댓글 정보
	 */
	@Operation(summary = "게시글 댓글 조회")
	@GetMapping
	public ResponseEntity<CommonResponses<CommentListFindResponse.CommentListItem>> getCommentsByFeedId(
		@RequestParam("feedId") @Positive Long feedId,
		@RequestParam(value = "pageSize", defaultValue = "10") int size,
		@RequestParam(value = "pageIdx", defaultValue = "0") int page
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return new ResponseEntity<>(commentService.getCommentsByFeedId(feedId, pageable), HttpStatus.OK);
	}

	/**
	 * 댓글 id로 조회
	 *
	 * @author 박형우
	 * @param commentId 댓글 id
	 * @return id로 조횐된 댓글
	 */
	@Operation(summary = "댓글 단일 조회")
	@GetMapping("/{commentId}")
	public ResponseEntity<CommonResponse<CommentDetailAndUpdateResponse>> getCommentById(
		@PathVariable("commentId") @Positive Long commentId) {
		return new ResponseEntity<>(commentService.getCommentById(commentId), HttpStatus.OK);
	}

	/**
	 * 댓글 수정
	 *
	 * @author 박형우
	 * @param commentId 댓글 id
	 * @param request 수정할 댓글 content
	 * @param authUser 로그인한 사용자 정보
	 * @return 수정된 댓글 정보
	 */
	@Operation(summary = "댓글 수정", security = {@SecurityRequirement(name = "bearer-key")})
	@PutMapping("/{commentId}")
	public ResponseEntity<CommonResponse<CommentDetailAndUpdateResponse>> updateComment(
		@PathVariable("commentId") @Positive Long commentId,
		@Valid @RequestBody CommentUpdateRequest request,
		@AuthenticationPrincipal MemberAuth authUser
	) {
		return new ResponseEntity<>(commentService.updateComment(commentId, request, authUser), HttpStatus.OK);
	}

	/**
	 * 댓글 삭제
	 *
	 * @author 박형우
	 * @param commentId 삭제할 댓글 id
	 * @param authUser 로그인한 사용자 정보
	 * @return 삭제 성공 여부
	 */
	@Operation(summary = "댓글 삭제", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/{commentId}")
	public ResponseEntity<CommonResponse<Long>> deleteComment(
		@PathVariable("commentId") @Positive Long commentId,
		@AuthenticationPrincipal MemberAuth authUser
	) {
		return new ResponseEntity<>(commentService.deleteByCommentId(commentId, authUser), HttpStatus.OK);
	}
}
