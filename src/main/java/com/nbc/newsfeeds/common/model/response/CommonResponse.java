package com.nbc.newsfeeds.common.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
	private boolean success;
	private int status;
	private String message;
	private T result;

	public static <T> CommonResponse<T> of(ResponseCode responseCode, T result) {
		return CommonResponse.<T>builder()
			.success(responseCode.isSuccess())
			.status(responseCode.getHttpStatus().value())
			.message(responseCode.getMessage())
			.result(result)
			.build();
	}

	public static <T> CommonResponse<T> of(boolean success, int status, String message) {
		return CommonResponse.<T>builder()
			.success(success)
			.status(status)
			.message(message)
			.build();
	}

	public static <T> CommonResponse<T> from(ResponseCode responseCode) {
		return CommonResponse.<T>builder()
			.success(responseCode.isSuccess())
			.status(responseCode.getHttpStatus().value())
			.message(responseCode.getMessage())
			.build();
	}

	public static <T> CommonResponse<T> success(int statusCode, T result) {
		return CommonResponse.<T>builder()
			.success(true)
			.status(statusCode)
			.result(result)
			.build();
	}
}
