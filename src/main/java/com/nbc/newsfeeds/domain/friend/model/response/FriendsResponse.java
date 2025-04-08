package com.nbc.newsfeeds.domain.friend.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FriendsResponse extends CursorPage {

	@Schema(description = "친구 목록")
	private List<FriendResponse> friends;

	public FriendsResponse(List<FriendResponse> friends, Long nextCursor, boolean hasNext) {
		super(nextCursor, hasNext);
		this.friends = friends;
	}

}
