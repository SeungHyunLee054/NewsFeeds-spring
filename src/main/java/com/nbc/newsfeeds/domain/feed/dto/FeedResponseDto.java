package com.nbc.newsfeeds.domain.feed.dto;

import java.time.LocalDateTime;

import com.nbc.newsfeeds.domain.feed.entity.Feed;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FeedResponseDto {
	private Long feedId;
	private Long memberId;
	private String name;
	private String title;
	private String content;
	private Integer heartCount;
	private Integer commentCount;
	private LocalDateTime feedCreatedAt;
	private LocalDateTime feedModifiedAt;

	@Builder
	public FeedResponseDto(Long feedId, Long memberId, String name, String title, String content, Integer heartCount, Integer commentCount, LocalDateTime feedCreatedAt, LocalDateTime feedModifiedAt){
		this.feedId = feedId;
		this.memberId = memberId;
		this.name = name;
		this.title = title;
		this.content = content;
		this.heartCount = heartCount;
		this.commentCount = commentCount;
		this.feedCreatedAt = feedCreatedAt;
		this.feedModifiedAt = feedModifiedAt;
	}

	public static FeedResponseDto fromEntity(Feed feed){
		return FeedResponseDto.builder()
			.feedId(feed.getFeedId())
			.memberId(feed.getMemberId())
			.name(feed.getName())
			.title(feed.getTitle())
			.content(feed.getContent())
			.heartCount(feed.getHeartCount())
			.commentCount(feed.getCommentCount())
			.feedCreatedAt(feed.getFeedCreatedAt())
			.feedModifiedAt(feed.getFeedModifiedAt()).build();
	}
}
