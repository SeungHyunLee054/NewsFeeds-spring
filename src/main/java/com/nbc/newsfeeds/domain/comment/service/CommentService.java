package com.nbc.newsfeeds.domain.comment.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.domain.comment.dto.request.CommentCreateRequest;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentUpdateRequest;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentCreateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentDetailAndUpdateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentResponse;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.comment.exception.CommentException;
import com.nbc.newsfeeds.domain.comment.exception.CommentExceptionCode;
import com.nbc.newsfeeds.domain.comment.repository.CommentRepository;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final MemberRepository memberRepository;

	public CommentResponse createComment(Long feedId, CommentCreateRequest create) {
		Member authUser = getAuthenticatedMember();

		// Feed 조회
		// TODO 404 게시글 조회 실패
		// Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new RuntimeException("피드 없음"));

		Comment comment = Comment.builder()
			.content(create.getContent())
			.member(authUser)
			// .feed(feed)
			.build();

		commentRepository.save(comment);

		CommentCreateResponse result = CommentCreateResponse.builder()
			.commentId(comment.getId())
			// .feedId(feed.getId())
			.memberId(authUser.getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.modifiedAt(comment.getModifiedAt())
			.build();

		return CommentResponse.builder()
			.success(true)
			.status(HttpStatus.CREATED.value())
			.message("댓글 생성 성공")
			.result(result)
			.build();
	}

	public CommentResponse getCommentsByFeedId(Long feedId, Pageable pageable) {

		// Feed 조회
		// TODO 404 게시글 조회 실패
		// Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new RuntimeException("해당 피드가 존재하지 않습니다."));

		// 댓글 조회
		// Page<Comment> page = commentRepository.findAllByFeed(feed, pageable);

		// DTO 리스트로 변환
		// List<CommentListFindResponse.CommentListItem> commentList = page.getContent().stream()
		// 	.map(CommentListFindResponse.CommentListItem::from)
		// 	.toList();

		// CommentListResult 생성
		// CommentListFindResponse result = CommentListFindResponse.builder()
		// .totalElements(page.getTotalElements())
		// .totalPage(page.getTotalPages())
		// .hasNextPage(page.hasNext())
		// .hasPreviousPage(page.hasPrevious())
		// .comments(commentList)
		// .build();

		return CommentResponse.builder()
			.success(true)
			.status(HttpStatus.OK.value())
			.message("댓글 목록 조회 성공")
			// .result(result)
			.build();
	}

	public CommentResponse getCommentById(Long commentId) {
		// TODO 404 댓글 조회 실패
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));

		CommentDetailAndUpdateResponse result = CommentDetailAndUpdateResponse.builder()
			.commentId(comment.getId())
			// .feedId(comment.getFeed().getId())
			.memberId(comment.getMember().getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.modifiedAt(comment.getModifiedAt())
			.build();

		return CommentResponse.builder()
			.success(true)
			.status(HttpStatus.OK.value())
			.message("댓글 단건 조회 성공")
			.result(result)
			.build();
	}

	@Transactional
	public CommentResponse updateComment(Long commentId, CommentUpdateRequest request) {
		Member authUser = getAuthenticatedMember();

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));

		if (!comment.getMember().getId().equals(authUser.getId())) {
			throw new CommentException(CommentExceptionCode.UNAUTHORIZED_ACCESS);
		}

		comment.update(request.getContent());

		CommentDetailAndUpdateResponse result = CommentDetailAndUpdateResponse.builder()
			.commentId(comment.getId())
			// .feedId(comment.getFeed().getId())
			.memberId(comment.getMember().getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.modifiedAt(comment.getModifiedAt())
			.build();

		return CommentResponse.builder()
			.success(true)
			.status(HttpStatus.OK.value())
			.message("댓글 수정 성공")
			.result(result)
			.build();
	}

	@Transactional
	public CommentResponse deleteByCommentId(Long commentId) {
		Member authUser = getAuthenticatedMember();

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));

		if (!comment.getMember().getId().equals(authUser.getId())) {
			throw new CommentException(CommentExceptionCode.UNAUTHORIZED_ACCESS);
		}

		commentRepository.deleteById(comment.getId());

		return CommentResponse.builder()
			.success(true)
			.status(HttpStatus.OK.value())
			.message("댓글 삭제 성공")
			.result(commentId)
			.build();
	}

	private Member getAuthenticatedMember() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MemberAuthDto authUser = (MemberAuthDto) authentication.getPrincipal();
		return memberRepository.findById(authUser.getId())
			.orElseThrow(() -> new CommentException(CommentExceptionCode.MEMBER_NOT_FOUND));
	}

}