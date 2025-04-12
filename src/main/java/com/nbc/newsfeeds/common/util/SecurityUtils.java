package com.nbc.newsfeeds.common.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
	public static String getCurrentToken() {
		return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
	}

	public static void clearContext() {
		SecurityContextHolder.clearContext();
	}
}
