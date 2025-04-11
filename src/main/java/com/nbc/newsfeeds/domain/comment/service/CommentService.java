package com.nbc.newsfeeds.domain.comment.service;

import static com.nbc.newsfeeds.domain.comment.dto.response.CommentListFindResponse.*;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.common.response.CommonResponses;
import com.nbc.newsfeeds.domain.comment.code.CommentExceptionCode;
import com.nbc.newsfeeds.domain.comment.code.CommentSuccessCode;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentCreateRequest;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentUpdateRequest;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentCreateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentDetailAndUpdateResponse;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.comment.exception.CommentException;
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

	/**
	 * 댓글 생성
	 *
	 * @author 박형우
	 * @param feedId 댓글을 달 게시글 id
	 * @param create 작성할 댓글 내용
	 * @param authUser 로그인된 사용자 정보
	 * @return 작성된 댓글 정보
	 */
	@Transactional
	public CommonResponse<CommentCreateResponse> createComment(Long feedId, CommentCreateRequest create,
		MemberAuth authUser) {

		Member member = memberRepository.findById(authUser.getId())
			.orElseThrow(() -> new CommentException(CommentExceptionCode.MEMBER_NOT_FOUND));

		Feed feed = feedRepository.findById(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		Comment comment = Comment.builder()
			.content(create.getContent())
			.member(member)
			.feed(feed)
			.heartCount(0)
			.build();

		commentRepository.save(comment);
		feed.increaseCommentCount();

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

	/**
	 * 게시글 번호로 댓글 찾기
	 *
	 * @author 박형우
	 * @param feedId 게시글 id
	 * @param pageable 페이지객체
	 * @return 댓글 정보
	 */
	public CommonResponses<CommentListItem> getCommentsByFeedId(Long feedId, Pageable pageable) {
		Feed feed = feedRepository.findById(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		Page<Comment> page = commentRepository.findAllByFeedId(feed.getId(), pageable);

		Page<CommentListItem> mappedPage = page.map(CommentListItem::from);

		return CommonResponses.of(CommentSuccessCode.COMMENT_LIST_SUCCESS, mappedPage);
	}

	/**
	 * id로 댓글 찾기
	 *
	 * @author 박형우
	 * @param commentId 찾을 댓글 id
	 * @return 찾은 댓글 정보
	 */
	public CommonResponse<CommentDetailAndUpdateResponse> getCommentById(Long commentId) {

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));

		CommentDetailAndUpdateResponse result = CommentDetailAndUpdateResponse.from(comment);

		return CommonResponse.of(CommentSuccessCode.COMMENT_GET_SUCCESS, result);
	}

	/**
	 * 댓글 수정
	 *
	 * @author 박형우
	 * @param commentId 수정할 댓글 id
	 * @param request 수정할 댓글 내용(content)
	 * @param authUser 로그인한 사용자 정보
	 * @return 수정된 댓글 정보
	 */
	@Transactional
	public CommonResponse<CommentDetailAndUpdateResponse> updateComment(Long commentId, CommentUpdateRequest request,
		MemberAuth authUser) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));

		if (!authUser.getId().equals(comment.getMember().getId())
			&& !authUser.getId().equals(comment.getFeed().getMember().getId())) {
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

	/**
	 * 댓글 삭제
	 *
	 * @author 박형우
	 * @param commentId 삭제할 댓글 id
	 * @param authUser 로그인한 사용자 정보
	 * @return 댓글 삭제 여부
	 */
	@Transactional
	public CommonResponse<Long> deleteByCommentId(Long commentId, MemberAuth authUser) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));

		if (!authUser.getId().equals(comment.getMember().getId())
			&& !authUser.getId().equals(comment.getFeed().getMember().getId())) {
			throw new CommentException(CommentExceptionCode.UNAUTHORIZED_ACCESS);
		}

		comment.getFeed().decreaseCommentCount();
		commentRepository.deleteById(comment.getId());

		return CommonResponse.of(CommentSuccessCode.COMMENT_DELETE_SUCCESS, commentId);
	}

}
