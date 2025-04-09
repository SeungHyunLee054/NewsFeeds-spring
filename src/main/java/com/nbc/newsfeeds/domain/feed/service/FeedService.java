package com.nbc.newsfeeds.domain.feed.service;

import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;

public interface FeedService {
	FeedResponseDto createFeed(Long userId, FeedRequestDto requestDto);
	FeedResponseDto getFeedById(Long feedId);
	CursorPageResponse<FeedResponseDto> getFeedByCursor(CursorPageRequest cursorPageRequest);
	void deleteFeed(Long userId, Long feedId);
	FeedResponseDto updateFeed(Long userId, Long feedId, FeedRequestDto requestDto);
}

