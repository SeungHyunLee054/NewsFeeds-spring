package com.nbc.newsfeeds.domain.heart.dto;

public class HeartResponseDto {
	private long likes;

	public HeartResponseDto(long likes) {
		this.likes = likes;
	}

	public long getLikes() {
		return likes;
	}
}
