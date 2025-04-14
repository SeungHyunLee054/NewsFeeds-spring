package com.nbc.newsfeeds.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.common.model.response.CursorPageResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendshipResponse;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, CursorPageResponse<FriendshipResponse>> friendCursorPageRedisTemplate(
		RedisConnectionFactory factory,
		ObjectMapper objectMapper
	) {
		RedisTemplate<String, CursorPageResponse<FriendshipResponse>> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);

		JavaType type = objectMapper.getTypeFactory()
			.constructParametricType(CursorPageResponse.class, FriendshipResponse.class);

		Jackson2JsonRedisSerializer<CursorPageResponse<FriendshipResponse>> serializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, type);

		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);

		return template;
	}
}
