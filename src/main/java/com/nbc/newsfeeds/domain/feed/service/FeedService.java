package com.nbc.newsfeeds.domain.feed.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;

public interface FeedService {
	FeedResponseDto createFeed(FeedRequestDto requestDto);
	FeedResponseDto getFeedById(Long feedId);
	Page<FeedResponseDto> getAllFeed(Pageable pageable);
	void deleteFeed(Long feedId);
	FeedResponseDto updateFeed(Long feedId, FeedRequestDto requestDto);
}
