package com.nbc.newsfeeds.domain.heart.dto;

import lombok.Getter;

@Getter
public class HeartResponseDto {
	private long likes;

	public HeartResponseDto(long likes) {
		this.likes = likes;
	}
}
