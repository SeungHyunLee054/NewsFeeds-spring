package com.nbc.newsfeeds.common.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CursorPage(
	@Schema(description = "다음 페이지에서 사용할 커서")
	Long nextCursor,

	@Schema(description = "다음 페이지 여부")
	Boolean hasNext
) {
}
