package com.nbc.newsfeeds.domain.heart.service;

import org.springframework.stereotype.Service;

import com.nbc.newsfeeds.domain.comment.code.CommentExceptionCode;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.comment.exception.CommentException;
import com.nbc.newsfeeds.domain.comment.repository.CommentRepository;
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
		if (!commentHeartRepository.existsByMember_IdAndComment_Id(memberId, commentId)
			&& findFeedOrThrow(feedId) != null) {
			Member member = findMemberOrThrow(memberId);
			Comment comment = findCommentOrThrow(commentId);
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
		if (!commentHeartRepository.existsByMember_IdAndComment_Id(memberId, commentId)
			&& findFeedOrThrow(feedId) != null) {
			throw new HeartException(HeartExceptionCode.NO_EXISTING_LIKE);
		} else {
			Comment comment = findCommentOrThrow(commentId);
			commentHeartRepository.deleteByMember_IdAndComment_Id(memberId, commentId);
			comment.decreaseHeartCount();
		}
	}

	/**
	 * 댓글 좋아요 조회<br>
	 * 댓글 ID 를 입력 받은 후, 댓글이 존재한다면 진행
	 * @param id 댓글 ID
	 * @author 박경오
	 */
	@Override
	@Transactional(readOnly = true)
	public HeartResponseDto viewHeart(long id) {
		Comment comment = findCommentOrThrow(id);
		return new HeartResponseDto(comment.getHeartCount());
	}

	private Comment findCommentOrThrow(long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentExceptionCode.COMMENT_NOT_FOUND));
	}
}
