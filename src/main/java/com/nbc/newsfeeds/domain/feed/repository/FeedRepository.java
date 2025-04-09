package com.nbc.newsfeeds.domain.feed.repository;

import java.util.List;
import java.util.Optional;


import com.nbc.newsfeeds.domain.feed.entity.Feed;

public interface FeedRepository {
	Feed save(Feed feed);

	Optional<Feed> findById(Long id);

	List<Feed> findByCursor(Long cursor, int size);

	Optional<Feed> findByIdWithMember(Long id);

}
