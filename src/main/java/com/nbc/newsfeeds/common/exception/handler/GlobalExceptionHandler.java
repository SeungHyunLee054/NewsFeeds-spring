package com.nbc.newsfeeds.common.exception.handler;

import java.time.DateTimeException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nbc.newsfeeds.common.exception.BaseException;
import com.nbc.newsfeeds.common.exception.dto.ValidationError;
import com.nbc.newsfeeds.common.model.response.CommonResponse;
import com.nbc.newsfeeds.common.model.response.CommonResponses;
import com.nbc.newsfeeds.common.model.response.ResponseCode;
import com.nbc.newsfeeds.common.util.LogUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<CommonResponse<ResponseCode>> handleBaseException(BaseException baseException) {
		LogUtils.logError(baseException);

		return ResponseEntity.status(baseException.getHttpStatus())
			.body(CommonResponse.from(baseException.getResponseCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CommonResponses<ValidationError>> inputValidationExceptionHandler(BindingResult result) {
		log.error(result.getFieldErrors().toString());

		List<ValidationError> validationErrors = result.getFieldErrors().stream()
			.map(fieldError -> ValidationError.builder()
				.field(fieldError.getField())
				.message(fieldError.getDefaultMessage())
				.code(fieldError.getCode())
				.build())
			.toList();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponses.of(false, HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다.",
				validationErrors));
	}

	@ExceptionHandler(DateTimeException.class)
	public ResponseEntity<CommonResponse<?>> handleDateTimeParseException() {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.of(false, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 날짜 입력입니다."));
	}
}
