package com.nbc.newsfeeds.domain.friend.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CursorPageRequest {
	@Schema(description = "이전 요청에서 마지막으로 본 친구 관계의 ID", nullable = true)
	private Long cursor;

	@Schema(description = "조회할 친구의 수", nullable = true)
	@Min(1)
	@Max(30)
	private Integer size = 10;
}
