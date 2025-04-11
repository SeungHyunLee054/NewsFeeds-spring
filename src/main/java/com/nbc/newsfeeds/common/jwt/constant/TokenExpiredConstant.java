package com.nbc.newsfeeds.common.jwt.constant;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenExpiredConstant {
	private static final long MILLISECOND = 1000L;

	@Value("${spring.jwt.token.access.second}")
	private long accessSecond;

	@Value("${spring.jwt.token.access.minute}")
	private long accessMinute;

	@Value("${spring.jwt.token.access.hour}")
	private long accessHour;

	@Value("${spring.jwt.token.refresh.second}")
	private long refreshSecond;

	@Value("${spring.jwt.token.refresh.minute}")
	private long refreshMinute;

	@Value("${spring.jwt.token.refresh.hour}")
	private long refreshHour;

	public long getAccessTokenExpiredTime() {
		return accessHour * accessMinute * accessSecond * MILLISECOND;
	}

	public long getRefreshTokenExpiredTime() {
		return refreshHour * refreshMinute * refreshSecond * MILLISECOND;
	}

	public long getRefreshTokenExpiredMinute() {
		return refreshHour * refreshMinute * refreshSecond;
	}

	public Date getAccessTokenExpiredDate(Date date) {
		return new Date(date.getTime() + getAccessTokenExpiredTime());
	}

	public Date getRefreshTokenExpiredDate(Date date) {
		return new Date(date.getTime() + getRefreshTokenExpiredTime());
	}
}
