package com.nbc.newsfeeds.common.model.response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponses<T> {
	private boolean success;
	private int status;
	private String message;
	private Result<T> result;

	@Getter
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Result<T> {
		private Long totalElements;
		private Integer totalPages;
		private Boolean hasNextPage;
		private Boolean hasPreviousPage;
		@Builder.Default
		private List<T> content = new ArrayList<>();
	}

	public static <T> CommonResponses<T> of(ResponseCode responseCode, Page<T> page) {
		return CommonResponses.<T>builder()
			.success(responseCode.isSuccess())
			.status(responseCode.getHttpStatus().value())
			.message(responseCode.getMessage())
			.result(Result.<T>builder()
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.hasNextPage(page.hasNext())
				.hasPreviousPage(page.hasPrevious())
				.content(page.getContent())
				.build())
			.build();
	}

	public static <T> CommonResponses<T> of(boolean success, int status, String message, List<T> list) {
		return CommonResponses.<T>builder()
			.success(success)
			.status(status)
			.message(message)
			.result(Result.<T>builder()
				.content(list)
				.build())
			.build();
	}
}
