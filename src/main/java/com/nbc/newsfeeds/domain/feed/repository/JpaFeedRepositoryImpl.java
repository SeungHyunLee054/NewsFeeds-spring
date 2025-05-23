package com.nbc.newsfeeds.domain.feed.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.nbc.newsfeeds.domain.feed.dto.FeedSearchCondition;
import com.nbc.newsfeeds.domain.feed.entity.Feed;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaFeedRepositoryImpl implements FeedRepository {

	private final EntityManager em;

	/**
	 * 게시글을 저장
	 *
	 * @param feed 저장할 게시글 엔티티
	 * @return 저장된 게시글 엔티티
	 * @author 기원
	 */
	@Override
	public Feed save(Feed feed) {
		em.persist(feed);
		return feed;
	}

	/**
	 * 게시글 ID로 게시글 단건 조회
	 *
	 * @param id 조회할 게시글 ID
	 * @return 존재하면 게시글, 없을 경우 Optional.empty()
	 * @author 기원
	 */
	@Override
	public Optional<Feed> findById(Long id) {
		return em.createQuery("SELECT F FROM Feed F WHERE F.id = :feed_id AND F.isDeleted = false", Feed.class)
			.setParameter("feed_id", id)
			.getResultList()
			.stream()
			.findFirst();
	}

	/**
	 * 커서 기반으로 게스글 전건 조회
	 *
	 * @param cursor 기준 커서
	 * @param size 조회할 게시글 수
	 * @return 조회된 게시글 리스트
	 * @author 기원
	 */
	@Override
	public List<Feed> findByCursor(Long cursor, int size) {
		StringBuilder jpql = new StringBuilder(
			"SELECT F FROM Feed F "
				+ "JOIN FETCH F.member "
				+ "WHERE F.isDeleted = false"
		);

		if (cursor != null && cursor > 0) {
			jpql.append(" AND F.id < :cursor");
		}

		jpql.append(" ORDER BY F.createdAt DESC");

		TypedQuery<Feed> query = em.createQuery(jpql.toString(), Feed.class);
		if (cursor != null && cursor > 0) {
			query.setParameter("cursor", cursor);
		}

		query.setMaxResults(size);
		return query.getResultList();
	}

	/**
	 * 게시글 ID로 작성자를 포함한 게시글 조회
	 *
	 * @param id 조회할 게시글 ID
	 * @return 조회된 게시글, 삭제된 게시글 제외
	 * @author 기원
	 */
	@Override
	public Optional<Feed> findByIdWithMember(Long id) {
		return em.createQuery(
				"SELECT F "
					+ "FROM Feed F JOIN FETCH F.member "
					+ "WHERE F.id = :id AND F.isDeleted = false",
				Feed.class)
			.setParameter("id", id)
			.getResultList()
			.stream()
			.findFirst();
	}

	/**
	 * 사용자가 좋아요한 게시글 목록 커서 기반 조회
	 *
	 * @param memberId 좋아요한 사용자 ID
	 * @param cursor 기준 커서
	 * @param size 조회할 피드 수
	 * @return 좋아요한 게시글 목록
	 * @author 기원
	 */
	@Override
	public List<Feed> findLikedFeedsByCursor(Long memberId, Long cursor, int size) {
		StringBuilder jpql = new StringBuilder(
			"SELECT f FROM Heart h "
				+ "JOIN h.feed f "
				+ "JOIN FETCH f.member "
				+ "WHERE h.member.id = :memberId "
				+ "AND f.isDeleted = false "
		);

		if (cursor != null && cursor > 0) {
			jpql.append("AND f.id < :cursor ");
		}

		jpql.append("ORDER BY f.createdAt DESC");

		TypedQuery<Feed> query = em.createQuery(jpql.toString(), Feed.class)
			.setParameter("memberId", memberId)
			.setMaxResults(size);

		if (cursor != null && cursor > 0) {
			query.setParameter("cursor", cursor);
		}

		return query.getResultList();
	}

	/**
	 * 친구들의 게시글 목록 커서 기반 조회
	 *
	 * @param friendIds 친구들의 ID 목록
	 * @param cursor 기준 커서
	 * @param size 조회할 피드 수
	 * @return 친구들의 게시글 목록
	 * @author 윤정환
	 */
	@Override
	public List<Feed> findFriendsFeedByCursor(Set<Long> friendIds, Long cursor, int size) {
		StringBuilder jpql = new StringBuilder(
			"""
				SELECT f FROM Feed f
				JOIN FETCH f.member
				WHERE f.isDeleted = false AND f.member.id in (:friendIds)
			"""
		);
		if (cursor != null && cursor > 0) {
			jpql.append(" AND f.id < :cursor");
		}
		jpql.append(" ORDER BY f.createdAt DESC");

		TypedQuery<Feed> query = em.createQuery(jpql.toString(), Feed.class)
			.setParameter("friendIds", friendIds)
			.setMaxResults(size);

		if (cursor != null && cursor > 0) {
			query.setParameter("cursor", cursor);
		}

		return query.getResultList();
	}

	/**
	 * 게시글 조회 (조건 기반 검색)
	 *
	 * @param condition 사용자가 입력한 검색 조건 DTO
	 * @return 조건에 부합하는 게시글 리스트
	 * @author 기원
	 */
	@Override
	public List<Feed> findBySearchCondition(FeedSearchCondition condition) {
		String jpql = buildQueryString(condition);
		TypedQuery<Feed> query = em.createQuery(jpql, Feed.class);
		setQueryParameters(query, condition);
		query.setMaxResults(condition.getSize());
		return query.getResultList();
	}

	/**
	 * 검색 조건에 따라 JPQL 쿼리 문자열을 동적으로 생성
	 * @author 기원
	 */
	private String buildQueryString(FeedSearchCondition condition) {
		StringBuilder jpql = new StringBuilder(
			"SELECT f FROM Feed f "
				+ "JOIN FETCH f.member "
				+ "WHERE f.isDeleted = false"
		);

		addStartDateCondition(jpql, condition);
		addEndDateCondition(jpql, condition);
		addCursorCondition(jpql, condition);
		addSortCondition(jpql, condition);

		return jpql.toString();
	}


	private void addStartDateCondition(StringBuilder jpql, FeedSearchCondition condition) {
		if (condition.getStartDate() != null) {
			jpql.append(" AND f.createdAt >= :startDate");
		}
	}

	private void addEndDateCondition(StringBuilder jpql, FeedSearchCondition condition) {
		if (condition.getEndDate() != null) {
			jpql.append(" AND f.createdAt <= :endDate");
		}
	}

	private void addCursorCondition(StringBuilder jpql, FeedSearchCondition condition) {
		if (condition.getCursor() != null && condition.getCursor() > 0) {
			jpql.append(" AND f.id < :cursor");
		}
	}

	private void addSortCondition(StringBuilder jpql, FeedSearchCondition condition) {
		switch (condition.getSort()) {
			case "likes" -> jpql.append(" ORDER BY f.heartCount DESC");
			case "comments" -> jpql.append(" ORDER BY f.commentCount DESC");
			default -> jpql.append(" ORDER BY f.modifiedAt DESC");
		}
	}

	private void setQueryParameters(TypedQuery<Feed> query, FeedSearchCondition condition) {
		setStartDateParam(query, condition);
		setEndDateParam(query, condition);
		setCursorParam(query, condition);
	}

	private void setStartDateParam(TypedQuery<Feed> query, FeedSearchCondition condition) {
		if (condition.getStartDate() != null) {
			query.setParameter("startDate", condition.getStartDate().atStartOfDay());
		}
	}

	private void setEndDateParam(TypedQuery<Feed> query, FeedSearchCondition condition) {
		if (condition.getEndDate() != null) {
			query.setParameter("endDate", condition.getEndDate().atTime(23, 59, 59));
		}
	}

	private void setCursorParam(TypedQuery<Feed> query, FeedSearchCondition condition) {
		if (condition.getCursor() != null && condition.getCursor() > 0) {
			query.setParameter("cursor", condition.getCursor());
		}
	}
}
