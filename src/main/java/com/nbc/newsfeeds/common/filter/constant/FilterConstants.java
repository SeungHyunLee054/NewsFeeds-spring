package com.nbc.newsfeeds.common.filter.constant;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilterConstants {
	public static final List<String> WHITE_LIST = List.of("/auth/signin", "/auth/signup",
		"/resources", "/swagger-ui", "/v3/api-docs", "/swagger-resources", "/webjars");
	public static final String REISSUE_URL = "/auth/reissue";
}
