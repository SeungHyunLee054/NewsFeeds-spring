package com.nbc.newsfeeds.domain.friend.entity;

import com.nbc.newsfeeds.common.audit.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
	name = "friendships",
	uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "friend_id"}),
	indexes = {
		@Index(name = "member_id_idx", columnList = "member_id"),
		@Index(name = "friend_id_idx", columnList = "friend_id")
	}
)
public class Friendship extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Long friendId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	FriendshipStatus status;

	@Builder
	public Friendship(Long memberId, Long friendId, FriendshipStatus status) {
		this.memberId = memberId;
		this.friendId = friendId;
		this.status = status;
	}

	public void updateStatus(FriendshipStatus status) {
		this.status = status;
	}
}
