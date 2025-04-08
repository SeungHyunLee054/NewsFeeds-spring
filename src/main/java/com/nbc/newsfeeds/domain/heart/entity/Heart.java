package com.nbc.newsfeeds.domain.heart.entity;

import org.springframework.data.annotation.Id;

import com.nbc.newsfeeds.common.audit.BaseEntity;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;

@Entity
@Builder
@Table(
	name = "heart",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"feed_id", "member_id"})
	})
public class Heart extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "heart_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "feed_id", nullable = false)
	private Feed feed;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

}
