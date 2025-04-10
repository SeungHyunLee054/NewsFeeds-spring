package com.nbc.newsfeeds.domain.feed.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.common.util.CursorPaginationUtil;
import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.exception.FeedBizException;
import com.nbc.newsfeeds.domain.feed.code.FeedExceptionCode;
import com.nbc.newsfeeds.domain.feed.repository.CommentCountRepository;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

	private final FeedRepository feedRepository;
	private final MemberRepository memberRepository;
	private final CommentCountRepository commentCountRepository;

	@Transactional
	@Override
	public FeedResponseDto createFeed(Long userId, FeedRequestDto requestDto) {
		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		Feed feed = Feed.builder()
			.member(member)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.heartCount(0)
			.commentCount(0)
			.isDeleted(false)
			.build();

		Feed savedFeed = feedRepository.save(feed);
		return FeedResponseDto.fromEntity(savedFeed);
	}

	@Transactional(readOnly = true)
	@Override
	public FeedResponseDto getFeedById(Long feedId) {
		Feed feed = feedRepository.findByIdWithMember(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		int commentCount = commentCountRepository.countByFeed_id(feedId);

		return FeedResponseDto.builder()
			.feedId(feed.getId())
			.memberId(feed.getMember().getId())
			.nickName(feed.getMember().getNickName())
			.title(feed.getTitle())
			.content(feed.getContent())
			.heartCount(feed.getHeartCount())
			.commentCount(commentCount)
			.feedCreatedAt(feed.getCreatedAt())
			.feedModifiedAt(feed.getModifiedAt())
			.build();
	}

	@Transactional(readOnly = true)
	@Override
	public CursorPageResponse<FeedResponseDto> getFeedByCursor(CursorPageRequest cursorPageRequest) {
		List<Feed> feeds = feedRepository.findByCursor(cursorPageRequest.getCursor(), cursorPageRequest.getSize());

		List<FeedResponseDto> dtoList = feeds.stream().map(feed -> {
				int commentCount = commentCountRepository.countByFeed_id(feed.getId());

				return FeedResponseDto.builder()
					.feedId(feed.getId())
					.memberId(feed.getMember().getId())
					.nickName(feed.getMember().getNickName())
					.title(feed.getTitle())
					.content(feed.getContent())
					.heartCount(feed.getHeartCount())
					.commentCount(commentCount)
					.feedCreatedAt(feed.getCreatedAt())
					.feedModifiedAt(feed.getModifiedAt())
					.build();
			}).toList();

		return CursorPaginationUtil.paginate(dtoList, cursorPageRequest.getSize(), FeedResponseDto::getFeedId);
	}

	@Transactional
	@Override
	public void deleteFeed(Long userId, Long feedId) {
		Feed feed = feedRepository.findById(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		if (!feed.getMember().getId().equals(userId)) {
			throw new FeedBizException(FeedExceptionCode.NOT_FEED_OWNER);
		}

		feed.markAsDelete();
		feedRepository.save(feed);
	}

	@Transactional
	@Override
	public FeedResponseDto updateFeed(Long userId, Long feedId, FeedRequestDto requestDto) {
		Feed feed = feedRepository.findById(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		if (!feed.getMember().getId().equals(userId)) {
			throw new FeedBizException(FeedExceptionCode.NOT_FEED_OWNER);
		}

		feed.update(requestDto.getTitle(), requestDto.getContent());

		int commentCount = commentCountRepository.countByFeed_id(feed.getId());

		return FeedResponseDto.builder()
			.feedId(feed.getId())
			.memberId(feed.getMember().getId())
			.nickName(feed.getMember().getNickName())
			.title(feed.getTitle())
			.content(feed.getContent())
			.heartCount(feed.getHeartCount())
			.commentCount(commentCount)
			.feedCreatedAt(feed.getCreatedAt())
			.feedModifiedAt(feed.getModifiedAt())
			.build();
	}

	@Override
	public CursorPageResponse<FeedResponseDto> getLikedFeedByCursor(CursorPageRequest cursorPageRequest,
		Long memberId) {
		List<Feed> feeds = feedRepository.findLikedFeedsByCursor(memberId, cursorPageRequest.getCursor(),
			cursorPageRequest.getSize());

		List<FeedResponseDto> dtoList = feeds.stream().map(feed -> {
				int commentCount = commentCountRepository.countByFeed_id(feed.getId());

				return FeedResponseDto.builder()
					.feedId(feed.getId())
					.memberId(feed.getMember().getId())
					.nickName(feed.getMember().getNickName())
					.title(feed.getTitle())
					.content(feed.getContent())
					.heartCount(feed.getHeartCount())
					.commentCount(commentCount)
					.feedCreatedAt(feed.getCreatedAt())
					.feedModifiedAt(feed.getModifiedAt())
					.build();
			})
			.toList();

		return CursorPaginationUtil.paginate(dtoList, cursorPageRequest.getSize(), FeedResponseDto::getFeedId);
	}
}
