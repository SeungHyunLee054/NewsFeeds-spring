package com.nbc.newsfeeds.domain.friend.model.request;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CursorPageRequest {
	@Schema(description = "이전 요청에서 마지막으로 본 친구 관계의 ID", nullable = true)
	private Long cursor;

	@Schema(description = "조회할 친구의 수", nullable = true)
	@Length(min = 1, max = 30)
	private Integer size = 10;
}
