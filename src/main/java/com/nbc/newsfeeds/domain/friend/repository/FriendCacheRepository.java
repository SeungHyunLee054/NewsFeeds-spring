package com.nbc.newsfeeds.domain.friend.repository;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import com.nbc.newsfeeds.common.config.CacheTtlProperties;
import com.nbc.newsfeeds.common.model.response.CursorPageResponse;
import com.nbc.newsfeeds.common.redis.constant.CacheNames;
import com.nbc.newsfeeds.common.util.CursorPaginationUtil;
import com.nbc.newsfeeds.domain.friend.model.response.FriendshipResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class FriendCacheRepository {

	private final RedisTemplate<String, CursorPageResponse<FriendshipResponse>> redisTemplate;
	private final CacheTtlProperties cacheTtlProperties;

	public void saveFriends(Long memberId, Long cursor, CursorPageResponse<FriendshipResponse> response) {
		String key = generateKey(memberId, cursor);
		Duration ttl = cacheTtlProperties.getTtl(CacheNames.FRIENDS);
		redisTemplate.opsForValue().set(key, response, ttl.getSeconds(), TimeUnit.SECONDS);
	}

	public CursorPageResponse<FriendshipResponse> getFriends(Long memberId, Long cursor, int size) {
		String key = generateKey(memberId, cursor);
		CursorPageResponse<FriendshipResponse> cached = redisTemplate.opsForValue().get(key);
		if (cached == null) {
			return null;
		}
		return CursorPaginationUtil.sliceForSize(cached, size);
	}

	public void evictFriends(Long memberId) {
		String pattern = CacheNames.FRIENDS + "::" + memberId + "::*";

		String luaScript =
			"""
				local cursor = '0'
				repeat
					local result = redis.call('SCAN', cursor, 'MATCH', KEYS[1], 'COUNT', 1000)
					cursor = result[1]
					local keys = result[2]
					if keys and #keys > 0 then
						for i = 1, #keys, 500 do
							local slice = {}
							for j = i, math.min(i + 499, #keys) do
								table.insert(slice, keys[j])
							end
							redis.call('DEL', unpack(slice))
						end
					end
				until cursor == '0'
				return true
				""";

		redisTemplate.execute(
			new DefaultRedisScript<>(luaScript, Boolean.class),
			Collections.singletonList(pattern)
		);
	}

	private String generateKey(Long memberId, Long cursor) {
		return CacheNames.FRIENDS + "::" + memberId + "::" + cursor;
	}
}
