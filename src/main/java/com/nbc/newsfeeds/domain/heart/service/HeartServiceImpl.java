package com.nbc.newsfeeds.domain.heart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.domain.feed.code.FeedExceptionCode;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.exception.FeedBizException;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;
import com.nbc.newsfeeds.domain.heart.entity.Heart;
import com.nbc.newsfeeds.domain.heart.exception.HeartException;
import com.nbc.newsfeeds.domain.heart.exception.HeartExceptionCode;
import com.nbc.newsfeeds.domain.heart.repository.HeartRepository;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.exception.MemberException;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService {

	private final HeartRepository heartRepository;
	private final MemberRepository memberRepository;
	private final FeedRepository feedRepository;

	/**
	 * 좋아요 추가<br>
	 * 게시글 ID, 멤버 ID 를 입력 받은 후, 좋아요 를 하지 않았다면 진행
	 * @param memberId 로그인 멤버 ID
	 * @param feedId 게시글 ID
	 * @author 박경오
	 */
	@Transactional
	public void addHeart(long memberId, long feedId) {
		if (!heartRepository.existsByMember_IdAndFeed_Id(memberId, feedId)) {
			Member member = findMemberOrThrow(memberId);
			Feed feed = findFeedOrThrow(feedId);
			Heart heart = Heart.builder()
				.feed(feed)
				.member(member)
				.build();
			heartRepository.save(heart);
			feed.increaseHeartCount();
		} else {
			throw new HeartException(HeartExceptionCode.DUPLICATE_LIKE_REQUEST);
		}
	}

	/**
	 * 좋아요 삭제<br>
	 * 게시글 ID, 멤버 ID 를 입력 받은 후, 좋아요 를 한적이 있다면 진행
	 * @param memberId 로그인 멤버 ID
	 * @param feedId 게시글 ID
	 * @author 박경오
	 */
	@Transactional
	public void cancelHeart(long memberId, long feedId) {
		if (!heartRepository.existsByMember_IdAndFeed_Id(memberId, feedId)) {
			throw new HeartException(HeartExceptionCode.NO_EXISTING_LIKE);
		} else {
			Feed feed = findFeedOrThrow(feedId);
			heartRepository.deleteByMember_IdAndFeed_Id(memberId, feedId);
			feed.decreaseHeartCount();
		}
	}

	/**
	 * 좋아요 확인<br>
	 * 게시글 ID를 입력 받은 후, 게시글이 존재한다면 좋아요 확인 진행
	 * @param feedId 게시글 ID
	 * @author 박경오
	 */
	@Transactional(readOnly = true)
	public HeartResponseDto viewHeart(long feedId) {
		Feed feed = findFeedOrThrow(feedId);
		return new HeartResponseDto(feed.getHeartCount());
	}

	private Member findMemberOrThrow(long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));
	}

	private Feed findFeedOrThrow(long feedId) {
		return feedRepository.findById(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));
	}

}
