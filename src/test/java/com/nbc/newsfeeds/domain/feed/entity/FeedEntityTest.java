package com.nbc.newsfeeds.domain.feed.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nbc.newsfeeds.domain.member.entity.Member;

class FeedEntityTest {

	private Feed getDefaultFeed() {
		return Feed.builder()
			.title("기본 제목")
			.content("기본 내용")
			.commentCount(0)
			.heartCount(0)
			.isDeleted(false)
			.member(Member.builder()
				.id(1L)
				.nickName("작성자")
				.email("test0@email.com")
				.password("password123")
				.phone("010-1234-5678")
				.build())
			.build();
	}

	@Test
	@DisplayName("update 메서드 - 제목과 내용 수정가능")
	void updateTest() {
		Feed feed = getDefaultFeed();

		feed.update("수정 제목", "수정 내용");

		assertThat(feed.getTitle()).isEqualTo("수정 제목");
		assertThat(feed.getContent()).isEqualTo("수정 내용");
	}

	@Test
	@DisplayName("markAsDelete 메서드 - isDeleted true 변경")
	void markAsDeleteTest() {
		Feed feed = getDefaultFeed();

		feed.markAsDelete();

		assertThat(feed.getIsDeleted()).isTrue();
	}

	@Test
	@DisplayName("increaseHeartCount 메서드 - 좋아요 수 1 증가")
	void increaseHeartCount() {
		Feed feed = getDefaultFeed();

		feed.increaseHeartCount();

		assertThat(feed.getHeartCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("decreaseHeartCount 메서드 - 좋아요 수가 1 감소한다")
	void decreaseHeartCountTest() {
		Feed feed = getDefaultFeed();
		feed.increaseHeartCount(); // 1

		feed.decreaseHeartCount();

		assertThat(feed.getHeartCount()).isEqualTo(0);
	}

	@Test
	@DisplayName("increaseCommentCount 메서드 - 댓글 수가 1 증가한다")
	void increaseCommentCountTest() {
		Feed feed = getDefaultFeed();

		feed.increaseCommentCount();

		assertThat(feed.getCommentCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("decreaseCommentCount 메서드 - 댓글 수가 1 감소한다")
	void decreaseCommentCountTest() {
		Feed feed = getDefaultFeed();
		feed.increaseCommentCount();

		feed.decreaseCommentCount();

		assertThat(feed.getCommentCount()).isEqualTo(0);
	}

}
