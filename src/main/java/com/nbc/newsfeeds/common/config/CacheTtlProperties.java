package com.nbc.newsfeeds.common.config;

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

		public Duration getTtl() {
			long hours = Optional.ofNullable(this.hours).orElse(0L);
			long minutes = Optional.ofNullable(this.minutes).orElse(0L);
			return Duration.ofHours(hours).plusMinutes(minutes);
		}
	}

	public Duration getTtl(String cacheName) {
		return Optional.ofNullable(values.get(cacheName))
			.map(TtlValue::getTtl)
			.orElse(Duration.ZERO);
	}
}
