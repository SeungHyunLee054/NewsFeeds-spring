package com.nbc.newsfeeds.domain.heart.entity;

import com.nbc.newsfeeds.common.audit.BaseEntity;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
	name = "comment_heart",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"member_id", "comment_id"})
	}
)
public class CommentHeart extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_heart_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "comment_id", nullable = false)
	private Comment comment;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

}
