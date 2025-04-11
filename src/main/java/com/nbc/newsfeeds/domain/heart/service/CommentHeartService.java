package com.nbc.newsfeeds.domain.heart.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.nbc.newsfeeds.domain.comment.code.CommentExceptionCode;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.comment.exception.CommentException;
import com.nbc.newsfeeds.domain.comment.repository.CommentRepository;
import com.nbc.newsfeeds.domain.feed.code.FeedExceptionCode;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.exception.FeedBizException;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;
import com.nbc.newsfeeds.domain.heart.entity.CommentHeart;
import com.nbc.newsfeeds.domain.heart.exception.HeartException;
import com.nbc.newsfeeds.domain.heart.exception.HeartExceptionCode;
import com.nbc.newsfeeds.domain.heart.repository.CommentHeartRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentHeartService extends AbstractHeartService {

	private final CommentRepository commentRepository;
	private final CommentHeartRepository commentHeartRepository;

	public CommentHeartService(MemberRepository memberRepository,
		FeedRepository feedRepository,
		CommentRepository commentRepository,
		CommentHeartRepository commentHeartRepository) {
		super(memberRepository, feedRepository);
		this.commentRepository = commentRepository;
		this.commentHeartRepository = commentHeartRepository;
	}

	/**
	 * 댓글 좋아요 추가<br>
	 * 게시글 ID, 멤버 ID, 댓글 ID 를 입력 받은 후, 좋아요 를 하지 않았다면 진행
	 * @param memberId 로그인 멤버 ID
	 * @param feedId 게시글 ID
	 * @param commentId 댓글 ID
	 * @author 박경오
	 */
	@Transactional
	public void addHeart(long memberId, long feedId, long commentId) {
		if (findFeedOrThrow(feedId) != null
			&& !commentHeartRepository.existsByMember_IdAndComment_Id(memberId, commentId)) {
			Member member = findMemberOrThrow(memberId);
			Comment comment = findCommentOrThrow(commentId);
			validateCommentBelongsToFeed(feedId, comment.getFeed().getId());
			CommentHeart commentHeart = CommentHeart.builder()
				.member(member)
				.comment(comment)
				.build();
			commentHeartRepository.save(commentHeart);
			comment.increaseHeartCount();
		} else {
			throw new HeartException(HeartExceptionCode.DUPLICATE_LIKE_REQUEST);
		}
	}

	/**
	 * 댓글 좋아요 취소<br>
	 * 게시글 ID, 멤버 ID, 댓글 ID 를 입력 받은 후, 좋아요 를 누른 적이 있다면 진행
	 * @param memberId 로그인 멤버 ID
	 * @param feedId 게시글 ID
	 * @param commentId 댓글 ID
	 * @author 박경오
	 */
	@Transactional
	public void cancelHeart(long memberId, long feedId, long commentId) {
		CommentHeart commentHeart = findCommentHeartOrThrow(memberId, commentId);
		verifyCommentAndFeedNotDeleted(commentHeart);
		Comment comment = commentHeart.getComment();
		Feed feed = comment.getFeed();
		validateCommentBelongsToFeed(feedId, feed.getId());
		commentHeartRepository.deleteByMember_IdAndComment_Id(memberId, commentId);
		comment.decreaseHeartCount();
	}

	/**
	 * 댓글 좋아요 조회<br>
	 * 댓글 ID 를 입력 받은 후, 댓글이 존재한다면 진행
	 * @param ids ids[0] : 게시글 ID, ids[1] : 댓글 ID
	 * @author 박경오
	 */
	@Override
	@Transactional(readOnly = true)
	public HeartResponseDto viewHeart(long... ids) {
		findFeedOrThrow(ids[0]);
		Comment comment = findCommentOrThrow(ids[1]);
		return new HeartResponseDto(comment.getHeartCount());
	}

	private Comment findCommentOrThrow(long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));
	}

	private CommentHeart findCommentHeartOrThrow(long memberId, long commentId) {
		return commentHeartRepository.findsByMember_IdAndComment_Id(memberId, commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));
	}

	private void verifyCommentAndFeedNotDeleted(CommentHeart commentHeart) {
		if (commentHeart.getComment() == null) {
			throw new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND);
		} else if (commentHeart.getComment().getFeed().getIsDeleted()) {
			throw new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND);
		}
	}

	private void validateCommentBelongsToFeed(long feedId, long targetFeedId) {
		if (!Objects.equals(feedId, targetFeedId)) {
			throw new HeartException(HeartExceptionCode.COMMENT_HEART_MISMATCH_EXCEPTION);
		}
	}
}
