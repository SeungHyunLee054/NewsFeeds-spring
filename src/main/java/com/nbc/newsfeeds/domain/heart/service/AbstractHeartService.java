package com.nbc.newsfeeds.domain.heart.service;

import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.domain.feed.code.FeedExceptionCode;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.exception.FeedBizException;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.exception.MemberException;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractHeartService {

	private final MemberRepository memberRepository;
	private final FeedRepository feedRepository;

	@Transactional
	protected Member findMemberOrThrow(long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberResponseCode.MEMBER_NOT_FOUND));
	}

	@Transactional
	public Feed findFeedOrThrow(long feedId) {
		return feedRepository.findById(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));
	}

	public abstract HeartResponseDto viewHeart(long Id);
}
