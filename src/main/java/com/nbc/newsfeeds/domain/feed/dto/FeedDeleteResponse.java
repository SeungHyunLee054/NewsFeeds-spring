package com.nbc.newsfeeds.domain.feed.dto;

/**
 *  게시글 삭제 응답 DTO
 *  삭제된 게시글의 ID를 클라이언트에 반환
 *
 * @param feedId 삭제된 게시글 ID
 * @author 기원
 */
public record FeedDeleteResponse(Long feedId) {

}
