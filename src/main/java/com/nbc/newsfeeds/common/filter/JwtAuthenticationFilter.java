package com.nbc.newsfeeds.common.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nbc.newsfeeds.common.filter.exception.FilterException;
import com.nbc.newsfeeds.common.filter.exception.FilterExceptionCode;
import com.nbc.newsfeeds.common.jwt.JwtTokenProvider;
import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;

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
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {
		String uri = request.getRequestURI();
		String authorization;
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

		if (!isWhiteList(uri)) {
			authorization = getTokenFromRequest(request);
			String token = resolveToken(authorization);

			if (jwtTokenProvider.isTokenExpired(token)) {
				throw new FilterException(FilterExceptionCode.TOKEN_EXPIRED);
			}

			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			securityContext.setAuthentication(authentication);
			SecurityContextHolder.setContext(securityContext);
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(String authorization) {
		if (authorization.isEmpty()) {
			throw new FilterException(FilterExceptionCode.EMPTY_TOKEN);
		}
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
