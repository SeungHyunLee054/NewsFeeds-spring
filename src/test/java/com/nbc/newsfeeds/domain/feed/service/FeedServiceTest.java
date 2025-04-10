package com.nbc.newsfeeds.domain.feed.service;

import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.domain.feed.code.FeedExceptionCode;
import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.exception.FeedBizException;
import com.nbc.newsfeeds.domain.feed.repository.CommentCountRepository;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;
import com.nbc.newsfeeds.domain.support.fixture.FixtureFactory;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

	@InjectMocks
	private FeedServiceImpl feedService;

	@Mock
	private FeedRepository feedRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private CommentCountRepository commentCountRepository;

	private Long memberId;
	private Member member;

	@BeforeEach
	void setUp() {
		Map<String, Object> fieldValues = new HashMap<>();
		fieldValues.put("id", 100L);
		fieldValues.put("email", FixtureFactory.generateRandomEmail());
		fieldValues.put("phone", FixtureFactory.generateRandomPhoneNumber());
		fieldValues.put("roles", List.of("ROLE_USER"));
		fieldValues.put("isDeleted", false);

		List<Member> members = FixtureFactory.createFixtures(1, Member.class, fieldValues);
		this.member = members.get(0);
		this.memberId = member.getId();
	}


	//성공 테스트
	@Test
	@DisplayName("게시글 등록 성공 - 단위 테스트")
	void createFeedTest(){
		FeedRequestDto requestDto = FeedRequestDto.builder()
			.title("테스트 제목")
			.content("테스트 내용")
			.build();

		given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

		Feed feed = Feed.builder()
			.id(1L)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.member(member)
			.isDeleted(false)
			.commentCount(0)
			.heartCount(0)
			.build();

		given(feedRepository.save(any(Feed.class))).willReturn(feed);

		FeedResponseDto responseDto = feedService.createFeed(memberId, requestDto);

		assertThat(responseDto.getTitle()).isEqualTo("테스트 제목");
		assertThat(responseDto.getContent()).isEqualTo("테스트 내용");
		assertThat(responseDto.getFeedId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("게시글 단건 조회 성공 - 단위 테스트")
	void getFeedTest() {

		Long feedId = 1L;

		Feed feed = Feed.builder()
			.id(feedId)
			.title("조회 제목")
			.content("조회 내용")
			.commentCount(0)
			.heartCount(10)
			.isDeleted(false)
			.member(member)
			.build();

		given(feedRepository.findByIdWithMember(anyLong())).willReturn(Optional.of(feed));
		given(commentCountRepository.countByFeed_id(anyLong())).willReturn(5);

		FeedResponseDto response = feedService.getFeedById(feedId);

		assertThat(response.getFeedId()).isEqualTo(feedId);
		assertThat(response.getTitle()).isEqualTo("조회 제목");
		assertThat(response.getContent()).isEqualTo("조회 내용");
		assertThat(response.getCommentCount()).isEqualTo(5);
		assertThat(response.getHeartCount()).isEqualTo(10);
	}

	@Test
	@DisplayName("게시글 전체 조회 성공 - 단위 테스트")
	void getAllFeedTest(){
		Long cursor = 0L;
		int size = 10;

		Feed feed1 = Feed.builder()
			.id(1L)
			.title("첫 번째 제목")
			.content("첫 번째 내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(member)
			.build();

		Feed feed2 = Feed.builder()
			.id(2L)
			.title("두 번째 제목")
			.content("두 번째 내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(member)
			.build();

		given(feedRepository.findByCursor(cursor, size)).willReturn(List.of(feed1, feed2));
		given(commentCountRepository.countByFeed_id(feed1.getId())).willReturn(3);
		given(commentCountRepository.countByFeed_id(feed2.getId())).willReturn(5);

		CursorPageResponse<FeedResponseDto> response = feedService.getFeedByCursor(new CursorPageRequest(cursor, size));

		assertThat(response.items().size()).isEqualTo(2);
		assertThat(response.pageInfo().hasNext()).isFalse();
		assertThat(response.items().get(0).getFeedId()).isEqualTo(1L);
		assertThat(response.items().get(1).getFeedId()).isEqualTo(2L);
	}

	@Test
	@DisplayName("게시글 수정 성공 - 단위 테스트")
	void updateFeedTest() {
		Long feedId = 1L;


		Feed originalFeed = Feed.builder()
			.id(feedId)
			.title("기존 제목")
			.content("기존 내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(member)
			.build();

		FeedRequestDto requestDto = FeedRequestDto.builder()
			.title("수정된 제목")
			.content("수정된 내용")
			.build();

		MemberAuth auth = MemberAuth.builder()
			.id(memberId)
			.build();

		given(feedRepository.findById(anyLong())).willReturn(Optional.of(originalFeed));

		FeedResponseDto responseDto = feedService.updateFeed(auth.getId(), feedId, requestDto);

		assertThat(responseDto.getTitle()).isEqualTo("수정된 제목");
		assertThat(responseDto.getContent()).isEqualTo("수정된 내용");
	}

	@Test
	@DisplayName("게시글 삭제 성공 - 단위 테스트")
	void deleteFeedTest(){
		Long feedId = 1L;

		Feed feed = Feed.builder()
			.id(feedId)
			.title("제목")
			.content("내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(member)
			.build();

		given(feedRepository.findById(anyLong())).willReturn(Optional.of(feed));

		feedService.deleteFeed(memberId, feedId);

		assertThat(feed.getIsDeleted()).isTrue();
	}

	//예외 처리 테스트

	//권한 없는 이용자 예외 테스트
	@Test
	@DisplayName("작성자가 아닌 사용자가 게시글 수정 시 예외 - 단위 테스트")
	void updateFeed_NotOwner_ExceptionTest() {

		Long feedId = 1L;
		Long otherUserId = 999L;

		Feed feed = Feed.builder()
			.id(feedId)
			.title("기존 제목")
			.content("기존 내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(member)
			.build();

		FeedRequestDto request = new FeedRequestDto("제목 수정", "내용 수정");

		given(feedRepository.findById(anyLong())).willReturn(Optional.of(feed));

		FeedBizException ex = assertThrows(FeedBizException.class, () ->
			feedService.updateFeed(otherUserId, feedId, request)
		);

		assertThat(ex.getResponseCode()).isEqualTo(FeedExceptionCode.NOT_FEED_OWNER);
	}

	@Test
	@DisplayName("작성자가 아닌 사용자가 게시글 삭제 시 예외 - 단위 테스트")
	void deleteFeed_NotOwner_ExceptionTest() {
		Long feedId = 1L;
		Long otherUserId = 999L;

		Feed feed = Feed.builder()
			.id(feedId)
			.title("제목")
			.content("내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(member)
			.build();

		given(feedRepository.findById(anyLong())).willReturn(Optional.of(feed));

		FeedBizException ex = assertThrows(FeedBizException.class, () ->
			feedService.deleteFeed(otherUserId, feedId)
		);

		assertThat(ex.getResponseCode()).isEqualTo(FeedExceptionCode.NOT_FEED_OWNER);
	}

	@Test
	@DisplayName("존재하지 않는 게시글 조회 시 예외 - 단위 테스트")
	void getFeed_NotFound_ExceptionTest() {
		Long feedId = 999L;

		given(feedRepository.findByIdWithMember(anyLong())).willReturn(Optional.empty());

		FeedBizException ex = assertThrows(FeedBizException.class, () ->
			feedService.getFeedById(feedId)
		);

		assertThat(ex.getResponseCode()).isEqualTo(FeedExceptionCode.FEED_NOT_FOUND);
	}

	@Test
	@DisplayName("존재하지 않는 게시글 수정 시 예외 - 단위 테스트")
	void updateFeed_NotFound_ExceptionTest() {
		Long feedId = 999L;
		Long userId = member.getId();

		FeedRequestDto requestDto = new FeedRequestDto("수정 제목", "수정 내용");

		given(feedRepository.findById(anyLong())).willReturn(Optional.empty());

		FeedBizException ex = assertThrows(FeedBizException.class, () ->
			feedService.updateFeed(userId, feedId, requestDto)
		);

		assertThat(ex.getResponseCode()).isEqualTo(FeedExceptionCode.FEED_NOT_FOUND);
	}

	@Test
	@DisplayName("존재하지 않는 게시글 삭제 시 예외 - 단위 테스트")
	void deleteFeed_NotFound_ExceptionTest() {
		Long feedId = 999L;
		Long userId = member.getId();

		given(feedRepository.findById(anyLong())).willReturn(Optional.empty());

		FeedBizException ex = assertThrows(FeedBizException.class, () ->
			feedService.deleteFeed(userId, feedId)
		);

		assertThat(ex.getResponseCode()).isEqualTo(FeedExceptionCode.FEED_NOT_FOUND);
	}
}
