package com.nbc.newsfeeds.domain.friend.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FriendResponse(
	@Schema(name = "친구 관계 ID")
	Long friendshipId,

	@Schema(name = "친구의 사용자 ID")
	Long friendId,

	@Schema(name = "친구의 이름")
	String name
) {
}
