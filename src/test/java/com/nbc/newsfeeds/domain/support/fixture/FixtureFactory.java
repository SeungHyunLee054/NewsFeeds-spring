package com.nbc.newsfeeds.domain.support.fixture;

import java.util.List;
import java.util.Map;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FailoverIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;

public class FixtureFactory {

	private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey.builder()
		.objectIntrospector(new FailoverIntrospector(
			List.of(
				FieldReflectionArbitraryIntrospector.INSTANCE
			)))
		.defaultNotNull(true)
		.build();

	public static <T> List<T> createFixtures(int size, Class<T> clazz, Map<String, ?> fieldValues) {
		ArbitraryBuilder<T> entityBuilder = FixtureFactory.FIXTURE_MONKEY
			.giveMeBuilder(clazz);
		fieldValues.forEach(entityBuilder::set);
		return entityBuilder.sampleList(size);
	}

	public static Arbitrary<String> generateRandomEmail() {
		return Arbitraries.strings()
			.withCharRange('a', 'z')
			.ofLength(8)
			.map(id -> id + "@test.com");
	}

	public static Arbitrary<String> generateRandomPhoneNumber() {
		return Arbitraries.integers().between(1000, 9999)
			.flatMap(firstPart -> Arbitraries.integers().between(1000, 9999)
				.map(secondPart -> "010-" + firstPart + "-" + secondPart));
	}
}
