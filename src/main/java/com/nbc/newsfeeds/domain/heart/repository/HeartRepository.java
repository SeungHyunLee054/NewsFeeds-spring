package com.nbc.newsfeeds.domain.heart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nbc.newsfeeds.domain.heart.entity.Heart;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
	boolean findByMember_IdAndFeed_Id(Long memberId, Long feedId);

	Long countByFeed_Id(Long feedId);

	void deleteByMember_IdAndFeed_Id(Long memberId, Long feedId);
}
