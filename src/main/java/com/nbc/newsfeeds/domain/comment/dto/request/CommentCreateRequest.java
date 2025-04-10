package com.nbc.newsfeeds.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentCreateRequest {
	@NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
	private String content;
}
