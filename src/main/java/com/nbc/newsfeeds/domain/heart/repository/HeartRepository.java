package com.nbc.newsfeeds.domain.heart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbc.newsfeeds.domain.heart.entity.Heart;

public interface HeartRepository extends JpaRepository<Heart, Long> {
	boolean existsByMember_IdAndFeed_Id(Long memberId, Long feedId);

	void deleteByMember_IdAndFeed_Id(Long memberId, Long feedId);
}
