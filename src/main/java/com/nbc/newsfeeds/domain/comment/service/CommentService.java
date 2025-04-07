package com.nbc.newsfeeds.domain.comment.service;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.domain.comment.dto.request.CommentCreateRequest;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentCreateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentDetailAndUpdateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentResponse;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentUpdateRequest;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
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
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MemberAuthDto authUser = (MemberAuthDto)authentication.getPrincipal();

		// TODO 401 Unauthorized
		Member member = memberRepository.findById(authUser.getId()).orElseThrow(() -> new RuntimeException("사용자 없음"));

		// Feed 조회
		// TODO 404 게시글 조회 실패
		// Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new RuntimeException("피드 없음"));

		Comment comment = Comment.builder()
			.content(create.getContent())
			.member(member)
			// .feed(feed)
			.build();

		commentRepository.save(comment);


		CommentCreateResponse result = CommentCreateResponse.builder()
			.commentId(comment.getId())
			// .feedId(feed.getId())
			.memberId(member.getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.modifiedAt(comment.getModifiedAt())
			.build();

		return CommentResponse.builder()
			.success(true)
			.status(201)
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
			.status(200)
			.message("댓글 목록 조회 성공")
			// .result(result)
			.build();
	}

	public CommentResponse getCommentById(Long commentId) {
		// TODO 404 댓글 조회 실패
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

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
			.status(200)
			.message("댓글 단건 조회 성공")
			.result(result)
			.build();
	}

	@Transactional
	public CommentResponse updateComment(Long commentId, CommentUpdateRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MemberAuthDto authUser = (MemberAuthDto)authentication.getPrincipal();

		// TODO 404 댓글 조회 실패
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

		// Exception 핸들러가 없어서 RuntimeException
		// TODO 401 작성한 본인이 아님
		if (!comment.getMember().getId().equals(authUser.getId())) {
			throw new RuntimeException("작성자가 아닙니다.");
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
			.status(200)
			.message("댓글 수정 성공")
			.result(result)
			.build();
	}

	@Transactional
	public CommentResponse deleteByCommentId(Long commentId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MemberAuthDto authUser = (MemberAuthDto)authentication.getPrincipal();
		
		// TODO 404 댓글 조회 실패
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

		// TODO 401 작성자 본인 아님
		// Exception 핸들러가 없어서 RuntimeException
		if (!comment.getMember().getId().equals(authUser.getId())) {
			throw new RuntimeException("작성자가 아닙니다.");
		}

		commentRepository.deleteById(comment.getId());

		return CommentResponse.builder()
			.success(true)
			.status(200)
			.message("댓글 삭제 성공")
			.result(commentId)
			.build();
	}
}