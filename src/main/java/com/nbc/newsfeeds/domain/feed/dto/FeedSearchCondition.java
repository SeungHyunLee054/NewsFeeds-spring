package com.nbc.newsfeeds.domain.feed.dto;

import java.time.LocalDate;

import com.nbc.newsfeeds.common.request.CursorPageRequest;

import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 조회 조건 DTO
 * 커서기반 페이징 + 정렬 + 기간 필터링
 *
 * @author 기원
 */
@Getter
@Setter
public class FeedSearchCondition extends CursorPageRequest {

	/**
	 * 조회 시작일
	 * weeks, months 값이 없을 경우 수동으로 지정 가능
	 */
	private LocalDate startDate;

	/**
	 *  조회 종료일
	 */
	private LocalDate endDate;

	/**
	 * 최근 N주간 조회를 위한 필드
	 * startDate, endDate 없을 시 우선 적용
	 */
	private Integer weeks;

	/**
	 * 최근 N개월 조회를 위한 필드
	 *  weeks 값이 없을 경우 weeks 로 자동 변환
	 */
	private Integer months;

	/**
	 * 정렬 기준
	 * Latest: 최신순
	 * likes: 좋아요 많은 순
	 * comments: 댓글 많은 순
	 */
	private String sort = "latest";

	/**
	 * 상대 기간 필드(months, weeks)를 기준으로 startDate 와 endDate 를 자동 계산
	 * startDate/endDate > weeks > months
	 */
	public void feedSearch() {
		if (months != null && weeks == null) {
			this.weeks = months * 4;
		}

		if ((startDate == null || endDate == null) && weeks != null) {
			this.startDate = LocalDate.now().minusWeeks(weeks);
			this.endDate = LocalDate.now();
		}
	}

}
