package com.nbc.newsfeeds.domain.comment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateResponse {
	private Long commentId;
	private Long feedId;
	private Long memberId;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
}
