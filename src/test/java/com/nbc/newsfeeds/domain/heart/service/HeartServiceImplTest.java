package com.nbc.newsfeeds.domain.heart.service;

import static org.mockito.BDDMockito.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nbc.newsfeeds.domain.heart.entity.Heart;
import com.nbc.newsfeeds.domain.heart.repository.HeartRepository;

/**
 * FAKE 테스트용 임시 Repository, Entity
 */
@ExtendWith(MockitoExtension.class)
class HeartServiceImplTest {

	interface MemberRepository {
		Member findById(Long id);
	}

	interface FeedRepository {
		Feed findById(Long id);
	}

	@InjectMocks
	private HeartService heartService;

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
		given(memberRepository.findById(memberId)).willReturn(new Member());
		given(feedRepository.findById(feedId)).willReturn(new Feed());

		//when
		heartService.addHeart(memberId, feedId);

		//then
		then(heartRepository).should().save(any(Heart.class));
	}
}