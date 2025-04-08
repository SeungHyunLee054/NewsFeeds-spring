package com.nbc.newsfeeds.domain.heart.service;

import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.heart.entity.Heart;
import com.nbc.newsfeeds.domain.heart.repository.HeartRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

/**
 * 좋아요 추가 기능 테스트(addHeart)
 */
@ExtendWith(MockitoExtension.class)
class HeartServiceImplTest {

	@InjectMocks
	private HeartServiceImpl heartService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FeedRepository feedRepository;

	@Mock
	private HeartRepository heartRepository;

	@Test
	@DisplayName("좋아요 추가 메서드 확인")
	void addHeart() {

		//given
		long memberId = 10L;
		long feedId = 30L;

		given(heartRepository.findByMember_IdAndFeed_Id(memberId, feedId)).willReturn(false);
		given(memberRepository.findById(memberId)).willReturn(Optional.of(new Member()));
		given(feedRepository.findById(feedId)).willReturn(Optional.of(new Feed()));

		//when
		heartService.addHeart(memberId, feedId);

		//then
		then(heartRepository).should().save(any(Heart.class));
	}
}