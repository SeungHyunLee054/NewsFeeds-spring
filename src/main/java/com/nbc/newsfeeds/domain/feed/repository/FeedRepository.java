package com.nbc.newsfeeds.domain.feed.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nbc.newsfeeds.domain.feed.entity.Feed;

public interface FeedRepository {
	Feed save(Feed feed);

	Optional<Feed> findById(Long id);

	Page<Feed> findAll(Pageable pageable);

	void delete(Feed feed);
}
