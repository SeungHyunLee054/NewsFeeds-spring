package com.nbc.newsfeeds.common.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nbc.newsfeeds.common.filter.exception.FilterException;
import com.nbc.newsfeeds.common.filter.exception.FilterExceptionCode;
import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.common.jwt.core.JwtService;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseJwtTokenFilter extends OncePerRequestFilter {
	private final JwtService jwtService;

	/**
	 * filter에서 token 인증<br>
	 * access token과 refresh token을 별도의 filter로 검증, 검증 로직은 동일하기 때문에 abstract class로 공통 로직 진행<br>
	 * 조건은 해당 filter에서 override, 인증 후 principal로 유저의 정보를 가져올 수 있음<br>
	 * 예외는 별도의 filter를 통해 handling하도록 함
	 * @param request 요청
	 * @param response 응답
	 * @param filterChain filterChain
	 * @throws ServletException 예외
	 * @throws IOException 예외
	 * @author 이승현
	 */
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {
		if (shouldSkip(request)) {
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

		if (shouldCheckBlackList() && jwtService.isBlackListed(token)) {
			throw new FilterException(FilterExceptionCode.ALREADY_SIGN_OUT);
		}

		MemberAuth memberAuth = jwtService.getMemberAuth(token);
		Authentication authentication = new UsernamePasswordAuthenticationToken(memberAuth, token,
			memberAuth.getAuthorities());

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

	protected abstract boolean shouldSkip(HttpServletRequest request);

	protected abstract String getTokenFromRequest(HttpServletRequest request);

	protected boolean shouldCheckBlackList() {
		return false;
	}
}
