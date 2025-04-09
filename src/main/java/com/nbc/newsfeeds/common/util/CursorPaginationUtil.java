package com.nbc.newsfeeds.common.util;

import java.util.List;
import java.util.function.Function;

import com.nbc.newsfeeds.common.response.CursorPage;
import com.nbc.newsfeeds.common.response.CursorPageResponse;

public class CursorPaginationUtil {
	public static <T> CursorPageResponse<T> paginate(List<T> items, int size, Function<T, Long> cursorExtractor) {
		boolean hasNext = items.size() > size;
		if (hasNext) {
			items = items.subList(0, size);
		}

		Long nextCursor = null;
		if (!items.isEmpty()) {
			nextCursor = cursorExtractor.apply(items.get(items.size() - 1));
		}

		return new CursorPageResponse<>(items, new CursorPage(nextCursor, hasNext));
	}
}
