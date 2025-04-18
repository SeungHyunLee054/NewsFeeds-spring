package com.nbc.newsfeeds.domain.friend.controller;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.nbc.newsfeeds.common.model.response.CommonResponse;

import lombok.NonNull;

@RestControllerAdvice(assignableTypes = FriendController.class)
public class FriendControllerAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType,
		@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
		Class<?> declaringClass = returnType.getDeclaringClass();
		return declaringClass.isAnnotationPresent(RestController.class)
			|| declaringClass.isAnnotationPresent(ResponseBody.class);
	}

	@Override
	public Object beforeBodyWrite(
		Object body,
		@NonNull MethodParameter returnType,
		@NonNull MediaType selectedContentType,
		@NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
		@NonNull ServerHttpRequest request,
		@NonNull ServerHttpResponse response
	) {
		if (body instanceof CommonResponse<?>) {
			return body;
		}

		int status = HttpStatus.OK.value();
		if (response instanceof ServletServerHttpResponse servletResponse) {
			status = servletResponse.getServletResponse().getStatus();
		}

		return CommonResponse.success(status, body);
	}
}
