package com.nbc.newsfeeds.domain.comment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.common.response.CommonResponses;
import com.nbc.newsfeeds.domain.comment.code.CommentSuccessCode;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentCreateRequest;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentUpdateRequest;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentCreateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentDetailAndUpdateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentListFindResponse;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.comment.exception.CommentException;
import com.nbc.newsfeeds.domain.comment.code.CommentExceptionCode;
import com.nbc.newsfeeds.domain.comment.repository.CommentRepository;
import com.nbc.newsfeeds.domain.feed.code.FeedExceptionCode;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.exception.FeedBizException;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final MemberRepository memberRepository;
	private final FeedRepository feedRepository;

	public CommonResponse<CommentCreateResponse> createComment(Long feedId, CommentCreateRequest create, MemberAuth authUser) {

		Member member = memberRepository.findById(authUser.getId()).orElseThrow(() -> new CommentException(CommentExceptionCode.MEMBER_NOT_FOUND));

		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		Comment comment = Comment.builder()
			.content(create.getContent())
			.member(member)
			.feed(feed)
			.build();

		commentRepository.save(comment);

		CommentCreateResponse result = CommentCreateResponse.builder()
			.commentId(comment.getId())
			.feedId(feed.getId())
			.memberId(authUser.getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.modifiedAt(comment.getModifiedAt())
			.build();

		return CommonResponse.of(CommentSuccessCode.COMMENT_CREATE_SUCCESS, result);
	}

	public CommonResponses<CommentListFindResponse.CommentListItem> getCommentsByFeedId(Long feedId, Pageable pageable) {

		// Feed 조회
		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		Page<Comment> page = commentRepository.findAllByFeedId(feed.getId(), pageable);

		List<CommentListFindResponse.CommentListItem> commentList = page.getContent().stream()
			.map(CommentListFindResponse.CommentListItem::from)
			.toList();

		CommentListFindResponse result = CommentListFindResponse.builder()
			.totalElements(page.getTotalElements())
			.totalPage(page.getTotalPages())
			.hasNextPage(page.hasNext())
			.hasPreviousPage(page.hasPrevious())
			.comments(commentList)
			.build();

		return CommonResponses.of(CommentSuccessCode.COMMENT_LIST_SUCCESS, page.map(CommentListFindResponse.CommentListItem::from));
	}

	public CommonResponse<CommentDetailAndUpdateResponse> getCommentById(Long commentId) {

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));

		CommentDetailAndUpdateResponse result = CommentDetailAndUpdateResponse.builder()
			.commentId(comment.getId())
			.feedId(comment.getFeed().getId())
			.memberId(comment.getMember().getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.modifiedAt(comment.getModifiedAt())
			.build();

		return CommonResponse.of(CommentSuccessCode.COMMENT_GET_SUCCESS, result);
	}

	@Transactional
	public CommonResponse<CommentDetailAndUpdateResponse> updateComment(Long commentId, CommentUpdateRequest request, MemberAuth authUser) {
		Member member = memberRepository.findById(authUser.getId()).orElseThrow(() -> new CommentException(CommentExceptionCode.MEMBER_NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));

		if (!comment.getMember().getId().equals(authUser.getId())) {
			throw new CommentException(CommentExceptionCode.UNAUTHORIZED_ACCESS);
		}

		comment.update(request.getContent());

		CommentDetailAndUpdateResponse result = CommentDetailAndUpdateResponse.builder()
			.commentId(comment.getId())
			.feedId(comment.getFeed().getId())
			.memberId(comment.getMember().getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.modifiedAt(comment.getModifiedAt())
			.build();


		return CommonResponse.of(CommentSuccessCode.COMMENT_UPDATE_SUCCESS, result);
	}

	@Transactional
	public CommonResponse<Long> deleteByCommentId(Long commentId, MemberAuth authUser) {
		Member member = memberRepository.findById(authUser.getId()).orElseThrow(() -> new CommentException(CommentExceptionCode.MEMBER_NOT_FOUND));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));

		if (!comment.getMember().getId().equals(authUser.getId())) {
			throw new CommentException(CommentExceptionCode.UNAUTHORIZED_ACCESS);
		}

		commentRepository.deleteById(comment.getId());

		return CommonResponse.of(CommentSuccessCode.COMMENT_DELETE_SUCCESS, commentId);
	}

}
