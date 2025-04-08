package com.nbc.newsfeeds.common.exception.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidationError {
	private String field;
	private String message;
	private String code;
}
