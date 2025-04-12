package com.nbc.newsfeeds.domain.feed.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.nbc.newsfeeds.domain.feed.dto.FeedSearchCondition;
import com.nbc.newsfeeds.domain.feed.entity.Feed;

public interface FeedRepository {
	Feed save(Feed feed);

	Optional<Feed> findById(Long id);

	List<Feed> findByCursor(Long cursor, int size);

	Optional<Feed> findByIdWithMember(Long id);

	List<Feed> findLikedFeedsByCursor(Long memberId, Long cursor, int size);

	List<Feed> findFriendsFeedByCursor(Set<Long> friendIds, Long cursor, int size);

	List<Feed> findBySearchCondition(FeedSearchCondition feedSearchCondition);
}
