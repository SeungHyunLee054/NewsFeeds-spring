package com.nbc.newsfeeds.domain.feed.validator;

import java.time.LocalDate;
import java.util.List;

import com.nbc.newsfeeds.domain.feed.code.FeedExceptionCode;
import com.nbc.newsfeeds.domain.feed.dto.FeedSearchCondition;
import com.nbc.newsfeeds.domain.feed.exception.FeedBizException;

public class FeedSearchValidator {

	private static final List<String> VALID_SORTS = List.of("latest", "likes", "comments");

	/**
	 * 전체 유효성 검사 수행
	 */
	public static void validate(FeedSearchCondition condition) {
		validateSort(condition.getSort());
		validateDateRange(condition.getStartDate(), condition.getEndDate());
	}

	/**
	 * 정렬 조건 검증
	 */
	private static void validateSort(String sort) {
		if (sort == null || !VALID_SORTS.contains(sort.toLowerCase())) {
			throw new FeedBizException(FeedExceptionCode.INVALID_SORT_TYPE);
		}
	}

	/**
	 * 날짜 범위 검증
	 */
	private static void validateDateRange(LocalDate start, LocalDate end) {
		if (start != null && end != null && start.isAfter(end)) {
			throw new FeedBizException(FeedExceptionCode.INVALID_DATE_RANGE);
		}
	}
}
