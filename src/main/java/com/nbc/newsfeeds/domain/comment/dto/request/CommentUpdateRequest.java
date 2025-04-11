package com.nbc.newsfeeds.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "댓글 수정 요청 DTO")
public class CommentUpdateRequest {
	@Schema(description = "수정할 댓글 내용")
	@NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
	private String content;
}
