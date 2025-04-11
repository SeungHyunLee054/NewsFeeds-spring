package com.nbc.newsfeeds.domain.comment.dto.response;

import java.time.LocalDateTime;

import com.nbc.newsfeeds.domain.comment.entity.Comment;

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
	@Schema(description = "좋아요 수")
	private Integer heartCount;

	/**
	 * Comment 엔티티를 CommentDetailAndUpdateResponse DTO 로 변환
	 */
	public static CommentDetailAndUpdateResponse from(Comment comment) {
		return CommentDetailAndUpdateResponse.builder()
			.commentId(comment.getId())
			.feedId(comment.getFeed().getId())
			.memberId(comment.getMember().getId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.modifiedAt(comment.getModifiedAt())
			.heartCount(comment.getHeartCount())
			.build();
	}

}
