package com.nbc.newsfeeds.domain.friend.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FriendRequestResponse(
	@Schema(description = "친구 관계 ID")
	Long friendshipId,

	@Schema(description = "친구 요청을 보낸 사용자 ID")
	Long memberId,

	@Schema(description = "친구 요청을 보낸 사용자 이름")
	String name
) {
}
