package com.nbc.newsfeeds.common.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.common.filter.exception.FilterException;
import com.nbc.newsfeeds.common.response.CommonResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

public class JwtExceptionFilter extends OncePerRequestFilter {
	/**
	 * 예외 처리 filter<br>
	 * 인증 과정 중 예외 발생시 해당 filter에서 catch하여 response를 보냄
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
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			filterChain.doFilter(request, response);
		} catch (FilterException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			objectMapper.writeValue(response.getWriter(),
				CommonResponse.from(e.getResponseCode()));
		}
	}
}
