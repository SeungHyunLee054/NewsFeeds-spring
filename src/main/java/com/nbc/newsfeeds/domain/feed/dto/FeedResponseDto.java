package com.nbc.newsfeeds.domain.feed.dto;

import java.time.LocalDateTime;

import com.nbc.newsfeeds.domain.feed.entity.Feed;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FeedResponseDto {
	private Long feedId;                        //게시글 ID
	private Long memberId;                        //작성자 ID
	private String nickName;                    //작성자 닉네임
	private String title;                        //게시글 제목
	private String content;                        //게시글 내용
	private Integer heartCount;                    //좋아요 수
	private Integer commentCount;                //댓글 수
	private LocalDateTime feedCreatedAt;        //게시글 생성 시각
	private LocalDateTime feedModifiedAt;        //게시글 수정 시각

	/**
	 * 게시글 응답 DTO 생성자
	 *
	 * @param feedId            게시글 ID
	 * @param memberId            작성자 ID
	 * @param nickName            작성자 닉네임
	 * @param title                게시글 제목
	 * @param content            게시글 내용
	 * @param heartCount        좋아요 수
	 * @param commentCount        댓글 수
	 * @param feedCreatedAt        생성 시각
	 * @param feedModifiedAt    수정 시각
	 * @author 기원
	 */
	@Builder
	public FeedResponseDto(Long feedId, Long memberId, String nickName, String title, String content,
		Integer heartCount, Integer commentCount, LocalDateTime feedCreatedAt, LocalDateTime feedModifiedAt) {
		this.feedId = feedId;
		this.memberId = memberId;
		this.nickName = nickName;
		this.title = title;
		this.content = content;
		this.heartCount = heartCount;
		this.commentCount = commentCount;
		this.feedCreatedAt = feedCreatedAt;
		this.feedModifiedAt = feedModifiedAt;
	}

	/**
	 * Feed 엔티티 기반으로 FeedResponseDto 객체 생성 - 기원
	 *
	 * @param feed Feed 엔티티
	 * @return FeedResponse 객체
	 * @author 기원
	 */
	public static FeedResponseDto fromEntity(Feed feed) {
		return FeedResponseDto.builder()
			.feedId(feed.getId())
			.memberId(feed.getMember().getId())
			.nickName(feed.getMember().getNickName())
			.title(feed.getTitle())
			.content(feed.getContent())
			.heartCount(feed.getHeartCount())
			.commentCount(feed.getCommentCount())
			.feedCreatedAt(feed.getCreatedAt())
			.feedModifiedAt(feed.getModifiedAt()).build();
	}
}
