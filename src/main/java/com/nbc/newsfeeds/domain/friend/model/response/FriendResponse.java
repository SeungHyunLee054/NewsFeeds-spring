package com.nbc.newsfeeds.domain.friend.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FriendResponse(
	@Schema(description = "친구의 사용자 ID")
	Long friendId,

	@Schema(description = "친구의 이름")
	String nickname
) {
}
