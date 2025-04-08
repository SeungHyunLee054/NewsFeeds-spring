package com.nbc.newsfeeds.domain.feed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.nbc.newsfeeds.domain.feed.entity.Feed;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaFeedRepositoryImpl implements FeedRepository {

	private final EntityManager em;

	@Override
	public Feed save(Feed feed) {
		em.persist(feed);
		return feed;
	}

	@Override
	public Optional<Feed> findById(Long id) {
		return em.createQuery("select f from Feed f where f.feedId = :feed_id and f.isDeleted = false", Feed.class)
			.setParameter("feed_id", id).getResultList().stream().findFirst();
	}

	@Override
	public Page<Feed> findAll(Pageable pageable) {
		List<Feed> feeds = em.createQuery("SELECT F FROM Feed F WHERE F.isDeleted = false ORDER BY F.feedCreatedAt DESC", Feed.class)
			.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

		Long totalCount = em.createQuery("select count(F) from Feed F where F.isDeleted = false", Long.class).getSingleResult();

		return new PageImpl<>(feeds, pageable, totalCount);
	}

	@Override
	public void delete(Feed feed) {
		feed.setIsDeleted(true);
	}
}
