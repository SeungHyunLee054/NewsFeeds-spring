package com.nbc.newsfeeds.domain.heart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;
import com.nbc.newsfeeds.domain.heart.entity.Heart;
import com.nbc.newsfeeds.domain.heart.exception.HeartException;
import com.nbc.newsfeeds.domain.heart.exception.HeartExceptionCode;
import com.nbc.newsfeeds.domain.heart.repository.FeedHeartRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

@Service
public class FeedHeartService extends AbstractHeartService {

	private final FeedHeartRepository feedHeartRepository;

	public FeedHeartService(MemberRepository memberRepository,
		FeedRepository feedRepository,
		FeedHeartRepository feedHeartRepository) {
		super(memberRepository, feedRepository);
		this.feedHeartRepository = feedHeartRepository;
	}

	/**
	 * 게시글 좋아요 추가<br>
	 * 게시글 ID, 멤버 ID 를 입력 받은 후, 좋아요 를 하지 않았다면 진행
	 * @param memberId 로그인 멤버 ID
	 * @param feedId 게시글 ID
	 * @author 박경오
	 */
	@Transactional
	public void addHeart(long memberId, long feedId) {
		Member member = findMemberOrThrow(memberId);
		Feed feed = findFeedOrThrow(feedId);
		if (!feedHeartRepository.existsByMember_IdAndFeed_Id(memberId, feedId)) {
			Heart heart = Heart.builder()
				.feed(feed)
				.member(member)
				.build();
			feedHeartRepository.save(heart);
			feed.increaseHeartCount();
		} else {
			throw new HeartException(HeartExceptionCode.DUPLICATE_LIKE_REQUEST);
		}
	}

	/**
	 * 게시글 좋아요 삭제<br>
	 * 게시글 ID, 멤버 ID 를 입력 받은 후, 좋아요 를 한적이 있다면 진행
	 * @param memberId 로그인 멤버 ID
	 * @param feedId 게시글 ID
	 * @author 박경오
	 */
	@Transactional
	public void cancelHeart(long memberId, long feedId) {
		Feed feed = findFeedOrThrow(feedId);
		if (!feedHeartRepository.existsByMember_IdAndFeed_Id(memberId, feedId)) {
			throw new HeartException(HeartExceptionCode.NO_EXISTING_LIKE);
		} else {
			feedHeartRepository.deleteByMember_IdAndFeed_Id(memberId, feedId);
			feed.decreaseHeartCount();
		}
	}

	/**
	 * 게시글 좋아요 조회<br>
	 * 게시글 ID를 입력 받은 후, 게시글이 존재한다면 좋아요 확인 진행
	 * @param ids ids[0] : 게시글 ID
	 * @author 박경오
	 */
	@Override
	@Transactional(readOnly = true)
	public HeartResponseDto viewHeart(long... ids) {
		Feed feed = findFeedOrThrow(ids[0]);
		return new HeartResponseDto(feed.getHeartCount());
	}
}
