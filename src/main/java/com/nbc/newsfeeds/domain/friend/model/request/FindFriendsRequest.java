package com.nbc.newsfeeds.domain.friend.model.request;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class FindFriendsRequest {
	private Long cursor;

	@Length(min = 1, max = 30)
	private Integer size = 10;
}
