package com.nbc.newsfeeds.domain.friend.model.request;

import jakarta.validation.constraints.NotNull;

public record RequestFriendRequest(
	@NotNull
	Long targetMemberId
) {
}
