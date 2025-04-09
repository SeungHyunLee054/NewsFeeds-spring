package com.nbc.newsfeeds.common.filter;

import com.nbc.newsfeeds.common.filter.constant.FilterConstants;
import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.common.jwt.core.JwtService;

import jakarta.servlet.http.HttpServletRequest;

public class AccessTokenFilter extends BaseJwtTokenFilter {
	public AccessTokenFilter(JwtService jwtService) {
		super(jwtService);
	}

	@Override
	protected boolean shouldSkip(HttpServletRequest request) {
		String uri = request.getRequestURI();

		return FilterConstants.WHITE_LIST.stream().anyMatch(uri::contains)
			|| uri.contains(FilterConstants.REISSUE_URL);
	}

	@Override
	protected String getTokenFromRequest(HttpServletRequest request) {
		return request.getHeader(JwtConstants.AUTH_HEADER);
	}

	@Override
	protected boolean shouldCheckBlackList() {
		return true;
	}
}
