package com.nbc.newsfeeds.domain.friend.entity;

import java.util.Objects;

import com.nbc.newsfeeds.common.audit.BaseEntity;
import com.nbc.newsfeeds.domain.friend.exception.FriendBizException;
import com.nbc.newsfeeds.domain.friend.exception.FriendExceptionCode;
import com.nbc.newsfeeds.domain.friend.model.request.FriendRequestDecision;

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
		@Index(name = "member_status_id_idx", columnList = "member_id, status, id DESC"),
		@Index(name = "friend_status_id_idx", columnList = "friend_id, status, id DESC")
	}
)
public class Friendship extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, name = "member_id")
	private Long memberId;

	@Column(nullable = false, name = "friend_id")
	private Long friendId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private FriendshipStatus status;

	@Builder
	public Friendship(Long memberId, Long friendId, FriendshipStatus status) {
		this.memberId = memberId;
		this.friendId = friendId;
		this.status = status;
	}

	public static Friendship of(Long memberId, Long friendId) {
		return Friendship.builder()
			.memberId(memberId)
			.friendId(friendId)
			.status(FriendshipStatus.PENDING)
			.build();
	}

	public void reRequest() {
		if (status == FriendshipStatus.PENDING) {
			throw new FriendBizException(FriendExceptionCode.ALREADY_REQUESTED);
		}
		if (status == FriendshipStatus.ACCEPTED) {
			throw new FriendBizException(FriendExceptionCode.ALREADY_FRIENDS);
		}
		this.status = FriendshipStatus.PENDING;
	}

	public void delete(Long memberId) {
		if (!Objects.equals(this.memberId, memberId) && !Objects.equals(this.friendId, memberId)) {
			throw new FriendBizException(FriendExceptionCode.NOT_FRIEND_PARTICIPANT);
		}
		if (this.status != FriendshipStatus.ACCEPTED) {
			throw new FriendBizException(FriendExceptionCode.NOT_ACCEPTED_REQUEST);
		}
		this.status = FriendshipStatus.DELETED;
	}

	public void respond(Long memberId, FriendRequestDecision status) {
		if (!Objects.equals(this.friendId, memberId)) {
			throw new FriendBizException(FriendExceptionCode.NOT_FRIEND_REQUEST_RECEIVER);
		}
		if (!Objects.equals(this.status, FriendshipStatus.PENDING)) {
			throw new FriendBizException(FriendExceptionCode.ALREADY_PROCESSED_REQUEST);
		}
		switch (status) {
			case ACCEPT -> this.accept();
			case DECLINE -> this.decline();
		}
	}

	public void cancel(Long memberId) {
		if (!Objects.equals(this.memberId, memberId)) {
			throw new FriendBizException(FriendExceptionCode.NOT_FRIEND_REQUEST_SENDER);
		}
		if (!Objects.equals(this.status, FriendshipStatus.PENDING)) {
			throw new FriendBizException(FriendExceptionCode.ALREADY_PROCESSED_REQUEST);
		}
		this.status = FriendshipStatus.CANCELLED;
	}

	private void accept() {
		this.status = FriendshipStatus.ACCEPTED;
	}

	private void decline() {
		this.status = FriendshipStatus.DECLINED;
	}
}
