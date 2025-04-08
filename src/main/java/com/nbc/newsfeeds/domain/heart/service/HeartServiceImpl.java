package com.nbc.newsfeeds.domain.heart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;
import com.nbc.newsfeeds.domain.heart.entity.Heart;
import com.nbc.newsfeeds.domain.heart.exception.HeartException;
import com.nbc.newsfeeds.domain.heart.exception.HeartExceptionCode;
import com.nbc.newsfeeds.domain.heart.repository.HeartRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService {

	private final HeartRepository heartRepository;
	private final MemberRepository memberRepository;
	private final FeedRepository feedRepository;

	@Transactional
	public void addHeart(long memberId, long feedId) {
		if (!heartRepository.existByMember_IdAndFeed_Id(memberId, feedId)) {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new HeartException(HeartExceptionCode.USER_NOT_EXIST));
			Feed feed = feedRepository.findById(feedId)
				.orElseThrow(() -> new HeartException(HeartExceptionCode.FEED_NOT_EXIST));
			Heart heart = Heart.builder()
				.feed(feed)
				.member(member)
				.build();
			heartRepository.save(heart);
		} else {
			throw new HeartException(HeartExceptionCode.DUPLICATE_LIKE_REQUEST);
		}
	}

	@Transactional
	public void cancelHeart(long memberId, long feedId) {
		if (!heartRepository.existByMember_IdAndFeed_Id(memberId, feedId)) {
			throw new HeartException(HeartExceptionCode.NO_EXISTING_LIKE);
		} else {
			heartRepository.deleteByMember_IdAndFeed_Id(memberId, feedId);
			// FIXME : new speed 의 heart_count 를 감소하는 로직 구현 필요
		}
	}

	@Transactional(readOnly = true)
	public HeartResponseDto viewHeart(long feedId) {
		return new HeartResponseDto(heartRepository.countByFeed_Id(feedId));
		// FIXME : new speed 의 heart_count 를 읽어오는 로직 구현 필요
	}

}