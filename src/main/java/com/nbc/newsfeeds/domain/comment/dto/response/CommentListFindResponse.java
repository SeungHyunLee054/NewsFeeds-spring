package com.nbc.newsfeeds.domain.comment.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.nbc.newsfeeds.domain.comment.entity.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 목록 조회 응답 DTO
 * <p>
 * 페이징된 댓글 목록과 메타데이터를 포함한 응답 형식입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 목록 조회 응답 DTO")
public class CommentListFindResponse {

	@Schema(description = "총 댓글 개수", example = "25")
	private long totalElements;

	@Schema(description = "총 페이지 수", example = "3")
	private int totalPage;

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	private boolean hasNextPage;

	@Schema(description = "이전 페이지 존재 여부", example = "false")
	private boolean hasPreviousPage;

	@Schema(description = "댓글 목록")
	private List<CommentListItem> comments;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(description = "댓글 목록 항목 DTO")
	public static class CommentListItem {

		@Schema(description = "댓글 ID")
		private Long commentId;

		@Schema(description = "댓글이 달린 게시글 ID")
		private Long feedId;

		@Schema(description = "댓글 작성자 ID")
		private Long memberId;

		@Schema(description = "댓글 내용")
		private String content;

		@Schema(description = "댓글 생성일시")
		private LocalDateTime createdAt;

		@Schema(description = "댓글 수정일시")
		private LocalDateTime modifiedAt;

		/**
		 * Comment 엔티티를 CommentListItem DTO로 변환
		 *
		 * @param comment 댓글 엔티티
		 * @return 변환된 CommentListItem
		 */
		public static CommentListItem from(Comment comment) {
			return CommentListItem.builder()
				.commentId(comment.getId())
				.feedId(comment.getFeed().getId())
				.memberId(comment.getMember().getId())
				.content(comment.getContent())
				.createdAt(comment.getCreatedAt())
				.modifiedAt(comment.getModifiedAt())
				.build();
		}
	}
}
