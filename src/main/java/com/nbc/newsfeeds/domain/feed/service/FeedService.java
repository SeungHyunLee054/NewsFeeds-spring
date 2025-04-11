package com.nbc.newsfeeds.domain.feed.service;

import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.domain.feed.dto.FeedDeleteResponse;
import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedSearchCondition;

public interface FeedService {
	FeedResponseDto createFeed(Long userId, FeedRequestDto requestDto);

	FeedResponseDto getFeedById(Long feedId);

	CursorPageResponse<FeedResponseDto> getFeedByCursor(CursorPageRequest cursorPageRequest);

	FeedDeleteResponse deleteFeed(Long userId, Long feedId);

	FeedResponseDto updateFeed(Long userId, Long feedId, FeedRequestDto requestDto);

	CursorPageResponse<FeedResponseDto> getLikedFeedByCursor(CursorPageRequest cursorPageRequest, Long memberId);

	CursorPageResponse<FeedResponseDto> searchFeeds(FeedSearchCondition searchCondition);
}

