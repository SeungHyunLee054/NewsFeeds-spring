package com.nbc.newsfeeds.common.filter;

import com.nbc.newsfeeds.common.filter.constant.FilterConstants;
import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.common.jwt.core.JwtService;

import jakarta.servlet.http.HttpServletRequest;

public class RefreshTokenFilter extends BaseJwtTokenFilter {
	public RefreshTokenFilter(JwtService jwtService) {
		super(jwtService);
	}

	@Override
	protected boolean shouldSkip(HttpServletRequest request) {
		return !request.getRequestURI().contains(FilterConstants.REISSUE_URL);
	}

	@Override
	protected String getTokenFromRequest(HttpServletRequest request) {
		return request.getHeader(JwtConstants.REFRESH_HEADER);
	}
}
