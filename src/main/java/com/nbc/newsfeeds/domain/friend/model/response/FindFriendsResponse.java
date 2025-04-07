package com.nbc.newsfeeds.domain.friend.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FindFriendsResponse(
	List<FriendResponse> friends,
	Long nextCursor,
	Boolean hasNext
) {
}
