package com.nbc.newsfeeds.domain.comment.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.nbc.newsfeeds.domain.comment.entity.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListFindResponse {
	private long totalElements;
	private int totalPage;
	private boolean hasNextPage;
	private boolean hasPreviousPage;
	private List<CommentListItem> comments;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommentListItem {
		private Long commentId;
		private Long feedId;
		private Long memberId;
		private String content;
		private LocalDateTime createdAt;
		private LocalDateTime modifiedAt;

		public static CommentListItem from(Comment comment) {
			return CommentListItem.builder()
				.commentId(comment.getId())
				.feedId(comment.getFeed().getFeedId())
				.memberId(comment.getMember().getId())
				.content(comment.getContent())
				.createdAt(comment.getCreatedAt())
				.modifiedAt(comment.getModifiedAt())
				.build();
		}
	}
}
