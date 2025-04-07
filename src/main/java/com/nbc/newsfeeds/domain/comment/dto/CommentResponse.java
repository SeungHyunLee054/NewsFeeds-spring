package com.nbc.newsfeeds.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
	private boolean success;
	private int status;
	private String message;
	private Object result;
}
