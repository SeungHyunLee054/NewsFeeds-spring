package com.nbc.newsfeeds.domain.friend.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record FriendRequestsResponse(
	@Schema(description = "친구 요청 목록")
	List<FriendRequestResponse> requests,

	@Schema(description = "커서 기반 페이지네이션 메타 정보")
	CursorPage pageInfo
) {
}
