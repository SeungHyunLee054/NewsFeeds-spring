package com.nbc.newsfeeds.domain.heart.service;

import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;

public interface HeartService {
	void addHeart(long memberId, long feedId);
	void cancelHeart(long memberId, long feedId);
	HeartResponseDto viewHeart(long feedId);
}
