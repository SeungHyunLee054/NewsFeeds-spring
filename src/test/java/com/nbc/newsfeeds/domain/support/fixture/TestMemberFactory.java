package com.nbc.newsfeeds.domain.support.fixture;

import static com.nbc.newsfeeds.domain.support.fixture.FixtureFactory.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nbc.newsfeeds.domain.member.entity.Member;

public class TestMemberFactory {

	public static Map<String, Object> defaultMemberFields() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("id", null);
		fields.put("email", generateRandomEmail());
		fields.put("phone", generateRandomPhoneNumber());
		fields.put("roles", List.of("ROLE_USER"));
		fields.put("isDeleted", false);
		return fields;
	}

	public static List<Member> createDefaultMembers(int count) {
		return createFixtures(count, Member.class, defaultMemberFields());
	}
}
