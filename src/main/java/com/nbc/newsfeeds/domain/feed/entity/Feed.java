package com.nbc.newsfeeds.domain.feed.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feed {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long feedId;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private String name;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime feedCreatedAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime feedModifiedAt;

	@Column(nullable = false)
	private Integer heartCount;

	@Column(nullable = false)
	private Integer commentCount;

	@Column(nullable = false)
	private Boolean isDeleted;

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void update(String title, String content){
		this.title = title;
		this.content = content;
	}
}
