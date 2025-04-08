package com.nbc.newsfeeds.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
	private boolean success;
	private int statusCode;
	private String message;
	private T result;

	public static <T> CommonResponse<T> of(ResponseCode responseCode, T result) {
		return CommonResponse.<T>builder()
			.success(responseCode.isSuccess())
			.statusCode(responseCode.getHttpStatus().value())
			.message(responseCode.getMessage())
			.result(result)
			.build();
	}

	public static <T> CommonResponse<T> from(ResponseCode responseCode) {
		return CommonResponse.<T>builder()
			.success(responseCode.isSuccess())
			.statusCode(responseCode.getHttpStatus().value())
			.message(responseCode.getMessage())
			.build();
	}

	public static <T> CommonResponse<T> success(int statusCode, T result) {
		return CommonResponse.<T>builder()
			.success(true)
			.statusCode(statusCode)
			.result(result)
			.build();
	}
}
