package com.nbc.newsfeeds.domain.friend.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FriendRequestsResponse extends CursorPageResponse {

	@Schema(description = "친구 요청 목록")
	private List<FriendRequestResponse> requests;

	public FriendRequestsResponse(List<FriendRequestResponse> requests, Long nextCursor, Boolean hasNext) {
		super(nextCursor, hasNext);
		this.requests = requests;
	}
}
