package com.nbc.newsfeeds.domain.friend.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FriendshipResponse(
	@Schema(description = "친구 관계 ID")
	Long friendshipId,

	@Schema(description = "친구 정보")
	FriendResponse friend
) {

	public FriendshipResponse(Long friendshipId, Long friendId, String friendNickname) {
		this(friendshipId, new FriendResponse(friendId, friendNickname));
	}
}
