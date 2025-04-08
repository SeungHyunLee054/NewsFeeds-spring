package com.nbc.newsfeeds.domain.heart.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;
import com.nbc.newsfeeds.domain.heart.entity.Heart;
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
		if (!heartRepository.findByMember_IdAndFeed_Id(memberId, feedId)) {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 사용자가 존재하지 않습니다."));
			Feed feed = feedRepository.findById(feedId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 피드가 조회되지 않습니다."));
			Heart heart = Heart.builder()
				.feed(feed)
				.member(member)
				.build();
			heartRepository.save(heart);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 사용자가 피드에 좋아요를 이미 남겼습니다.");
		}
	}

	@Transactional
	public void cancelHeart(long memberId, long feedId) {
		if (!heartRepository.findByMember_IdAndFeed_Id(memberId, feedId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 사용자가 피드에 좋아요를 남긴 기록이 없습니다.");
		} else {
			heartRepository.deleteByMember_IdAndFeed_Id(memberId, feedId);
			// FIXME : new speed 의 heart_count 를 취소하는 로직 구현 필요
		}
	}

	@Transactional(readOnly = true)
	public HeartResponseDto viewHeart(long feedId) {
		return new HeartResponseDto(heartRepository.countByFeed_Id(feedId));
		// FIXME : 임시구현
	}

}
