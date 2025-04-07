package com.nbc.newsfeeds.domain.friend.model.request;

import com.nbc.newsfeeds.domain.friend.constant.FriendshipStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RespondToFriendRequest(
	@Schema(description = "변경할 친구 관계의 상태")
	@NotNull
	FriendshipStatus status
) {
}
