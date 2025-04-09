package com.nbc.newsfeeds.domain.feed.entity;

import com.nbc.newsfeeds.common.audit.BaseEntity;
import com.nbc.newsfeeds.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feed extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "feed_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Integer heartCount;

	@Column(nullable = false)
	private Integer commentCount;

	@Column(nullable = false)
	private Boolean isDeleted;

	public void markAsDelete() {
		this.isDeleted = true;
	}

	public void update(String title, String content){
		this.title = title;
		this.content = content;
	}

}
