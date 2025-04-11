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
import com.nbc.newsfeeds.domain.heart.repository.FeedHeartRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;


/**
 * TODO : 테스트코드 추가 작성 필요
 */
@ExtendWith(MockitoExtension.class)
class FeedHeartServiceTest {

	@InjectMocks
	private FeedHeartService heartService;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private FeedRepository feedRepository;
	@Mock
	private FeedHeartRepository heartRepository;

	@Test
	@DisplayName("게시글 좋아요 추가 메서드 확인")
	void addHeart() {

		//given
		long memberId = 10L;
		long feedId = 30L;

		given(heartRepository.existsByMember_IdAndFeed_Id(memberId, feedId)).willReturn(false);
		given(memberRepository.findById(memberId)).willReturn(Optional.of(new Member()));
		given(feedRepository.findById(feedId)).willReturn(Optional.of(new Feed()));

		//when
		heartService.addHeart(memberId, feedId);

		//then
		then(heartRepository).should().save(any(Heart.class));
	}

	@Test
	void cancelHeart() {
	}

	@Test
	void viewHeart() {
	}
}








