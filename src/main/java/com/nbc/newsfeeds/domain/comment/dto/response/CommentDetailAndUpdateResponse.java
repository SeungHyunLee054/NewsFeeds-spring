package com.nbc.newsfeeds.domain.comment.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 수정, 조회 응답 DTO")
public class CommentDetailAndUpdateResponse {

	@Schema(description = "댓글 ID")
	private Long commentId;
	@Schema(description = "게시글 ID")
	private Long feedId;
	@Schema(description = "유저 ID")
	private Long memberId;
	@Schema(description = "댓글 내용")
	private String content;
	@Schema(description = "생성 일자")
	private LocalDateTime createdAt;
	@Schema(description = "수정 일자")
	private LocalDateTime modifiedAt;

}
