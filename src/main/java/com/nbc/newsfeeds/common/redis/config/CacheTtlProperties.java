package com.nbc.newsfeeds.common.redis.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Component
@ConfigurationProperties(prefix = "cache.ttl")
public class CacheTtlProperties {

	private final Map<String, TtlValue> values = new HashMap<>();

	@Getter
	@Setter
	public static class TtlValue {
		private Long hours;
		private Long minutes;

		public Duration getTTL() {
			long h = Optional.ofNullable(hours).orElse(0L);
			long m = Optional.ofNullable(minutes).orElse(0L);
			return Duration.ofHours(h).plusMinutes(m);
		}
	}

	public Duration getTTL(String cacheName) {
		return Optional.ofNullable(values.get(cacheName))
			.map(TtlValue::getTTL)
			.orElse(Duration.ZERO);
	}
}
