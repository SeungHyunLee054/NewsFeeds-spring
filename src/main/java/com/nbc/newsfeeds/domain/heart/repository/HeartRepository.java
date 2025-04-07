package com.nbc.newsfeeds.domain.heart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nbc.newsfeeds.domain.heart.entity.Heart;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
	Optional<Heart> findByMemberIdAndFeedId(Long memberId, Long feedId);

}
