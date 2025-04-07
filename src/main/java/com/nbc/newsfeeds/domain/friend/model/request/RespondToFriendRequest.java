package com.nbc.newsfeeds.domain.friend.model.request;

import com.nbc.newsfeeds.domain.friend.constant.FriendshipStatus;

import jakarta.validation.constraints.NotNull;

public record RespondToFriendRequest(
	@NotNull
	FriendshipStatus status
) {
}
