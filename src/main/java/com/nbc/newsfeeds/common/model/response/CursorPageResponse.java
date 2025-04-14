package com.nbc.newsfeeds.common.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CursorPageResponse<T>(
	@Schema(description = "조회된 목록")
	List<T> items,

	@Schema(description = "커서 기반 페이지네이션 메타 정보")
	CursorPage pageInfo
) {
}
