package com.nbc.newsfeeds.domain.friend.model.response;

public record FriendResponse(
	Long friendshipId,
	Long friendId,
	String name
) {
}
