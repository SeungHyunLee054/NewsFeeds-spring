package com.nbc.newsfeeds.domain.comment.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDetailAndUpdateResponse {

	private Long commentId;
	private Long feedId;
	private Long memberId;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

}
