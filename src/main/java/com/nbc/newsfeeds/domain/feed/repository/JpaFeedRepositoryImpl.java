package com.nbc.newsfeeds.domain.feed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nbc.newsfeeds.domain.feed.entity.Feed;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
		return em.createQuery("SELECT F FROM Feed F WHERE F.id = :feed_id AND F.isDeleted = false", Feed.class)
			.setParameter("feed_id", id)
			.getResultList()
			.stream()
			.findFirst();
	}

	@Override
	public List<Feed> findByCursor(Long cursor, int size) {
		String jpql = "SELECT F FROM Feed F JOIN FETCH F.member " + "WHERE F.isDeleted = false " + (cursor != null ? "AND F.id < :cursor " : "") + "ORDER BY F.id DESC";

		TypedQuery<Feed> query = em.createQuery(jpql, Feed.class);
		if (cursor != null){
			query.setParameter("cursor", cursor);
		}

		return query.getResultList();
	}

	@Override
	public Optional<Feed> findByIdWithMember(Long id) {
		return em.createQuery("SELECT F FROM Feed F JOIN FETCH F.member WHERE F.id = :id AND F.isDeleted = false", Feed.class)
			.setParameter("id", id)
			.getResultList()
			.stream()
			.findFirst();
	}

	@Override
	public List<Feed> findLikedFeedsByCursor(Long memberId, Long cursor, int size) {
		StringBuilder jpql = new StringBuilder(
			"SELECT f FROM Heart h " +
				"JOIN h.feed f " +
				"JOIN FETCH f.member " +
				"WHERE h.member.id = :memberId " +
				"AND f.isDeleted = false "
		);

		if (cursor != null) {
			jpql.append("AND f.id < :cursor ");
		}

		jpql.append("ORDER BY f.id DESC");

		TypedQuery<Feed> query = em.createQuery(jpql.toString(), Feed.class)
			.setParameter("memberId", memberId)
			.setMaxResults(size);

		if (cursor != null) {
			query.setParameter("cursor", cursor);
		}

		return query.getResultList();
	}
}
