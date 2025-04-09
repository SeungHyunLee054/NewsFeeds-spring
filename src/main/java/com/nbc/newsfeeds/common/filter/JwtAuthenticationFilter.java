package com.nbc.newsfeeds.common.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nbc.newsfeeds.common.filter.exception.FilterException;
import com.nbc.newsfeeds.common.filter.exception.FilterExceptionCode;
import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.common.jwt.core.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final List<String> WHITE_LIST = List.of("/auth/signin", "/auth/signup",
		"/resources", "/swagger-ui", "/v3/api-docs", "/swagger-resources", "/webjars");
	public static final String REISSUE_URL = "/auth/reissue";
	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {
		String uri = request.getRequestURI();

		if (isWhiteList(uri)) {
			filterChain.doFilter(request, response);
			return;
		}

		String authorization = getTokenFromRequest(request);
		if (authorization == null || authorization.isBlank()) {
			throw new FilterException(FilterExceptionCode.EMPTY_TOKEN);
		}

		String token = resolveToken(authorization);
		if (jwtService.isTokenExpired(token)) {
			throw new FilterException(FilterExceptionCode.TOKEN_EXPIRED);
		}

		String tokenType = jwtService.getTokenTypeFromToken(token);
		Authentication authentication;

		switch (tokenType) {
			case JwtConstants.REFRESH_TOKEN -> {
				if (!uri.startsWith(REISSUE_URL)) {
					throw new FilterException(FilterExceptionCode.INVALID_TOKEN_USAGE);
				}

				authentication = jwtService.getAuthentication(token);
			}
			case JwtConstants.ACCESS_TOKEN -> {
				if (jwtService.isBlackListed(token)) {
					throw new FilterException(FilterExceptionCode.ALREADY_SIGN_OUT);
				}

				authentication = jwtService.getAuthentication(token);
			}
			default -> throw new FilterException(FilterExceptionCode.MALFORMED_JWT_REQUEST);
		}

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		filterChain.doFilter(request, response);
	}

	private String resolveToken(String authorization) {
		if (!authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
			throw new FilterException(FilterExceptionCode.MALFORMED_JWT_REQUEST);
		}

		return authorization.substring(JwtConstants.TOKEN_PREFIX.length());
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		return request.getHeader(JwtConstants.AUTH_HEADER);
	}

	private boolean isWhiteList(String uri) {
		return WHITE_LIST.stream().anyMatch(uri::contains);
	}
}
