package com.nbc.newsfeeds.domain.friend.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RequestFriendRequest(
	@Schema(name = "요청 대상 사용자 ID")
	@NotNull
	Long targetMemberId
) {
}
