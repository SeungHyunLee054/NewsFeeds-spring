package com.nbc.newsfeeds.domain.feed.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.domain.feed.code.FeedSuccessCode;
import com.nbc.newsfeeds.domain.feed.dto.FeedDeleteResponse;
import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedSearchCondition;
import com.nbc.newsfeeds.domain.feed.service.FeedService;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

	private final FeedService feedService;

	/**
	 *  게시글 생성
	 *
	 * @param memberAuthDto 인증된 사용자 정보
	 * @param requestDto 게시글 제목과 내용이 담긴 요청 DTO
	 * @return 생성된 게시글 정보를 포함한 응답 DTO
	 * @author 기원
	 */
	@Operation(summary = "게시글 작성", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping
	public ResponseEntity<CommonResponse<FeedResponseDto>> createFeed(
		@AuthenticationPrincipal MemberAuth memberAuthDto,
		@Valid @RequestBody FeedRequestDto requestDto) {
		FeedResponseDto response = feedService.createFeed(memberAuthDto.getId(), requestDto);
		return ResponseEntity.status(FeedSuccessCode.FEED_CREATED.getHttpStatus())
			.body(CommonResponse.of(FeedSuccessCode.FEED_CREATED, response));
	}

	/**
	 * 게시글 ID 기반 단건 조회
	 *
	 * @param feedId 조회할 게시글 ID
	 * @return 해당 게시글 정보를 포함한 응답 DTO
	 * @author 기원
	 */
	@Operation(summary = "게시글 단건 조회(feedId 기반)", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/{feedId}")
	public ResponseEntity<CommonResponse<FeedResponseDto>> getFeed(@PathVariable Long feedId) {
		FeedResponseDto responseDto = feedService.getFeedById(feedId);
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_FOUND, responseDto));
	}

	/**
	 * 커서 기반 게시글 전건 조회
	 *
	 * @param req 커서 및 페이지 크기 정보를 담은 요청 DTO
	 * @return 페이징된 게시글 응답
	 * @author 기원
	 */
	@Operation(summary = "게시글 목록 조회(커서 기반)", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping
	public ResponseEntity<CommonResponse<CursorPageResponse<FeedResponseDto>>> getFeeds(
		@Valid @ModelAttribute CursorPageRequest req) {
		CursorPageResponse<FeedResponseDto> response = feedService.getFeedByCursor(req);
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_LISTED, response));
	}

	/**
	 * 게시글 수정
	 *
	 * @param memberAuthDto 인증된 사용자 정보
	 * @param feedId 수정할 게시글 ID
	 * @param requestDto 수정할 게시글 제목과 내용이 담긴 요청 DTO
	 * @return 수정된 게시글 정보를 포함한 응답 DTO
	 * @author 기원
	 */
	@Operation(summary = "게시글 수정(feedId 기반)", security = {@SecurityRequirement(name = "bearer-key")})
	@PutMapping("/{feedId}")
	public ResponseEntity<CommonResponse<FeedResponseDto>> updateFeed(
		@AuthenticationPrincipal MemberAuth memberAuthDto,
		@PathVariable Long feedId,
		@Valid @RequestBody FeedRequestDto requestDto) {
		FeedResponseDto responseDto = feedService.updateFeed(memberAuthDto.getId(), feedId, requestDto);
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_UPDATED, responseDto));
	}

	/**
	 * 게시글 삭제(Soft delete 처리)
	 *
	 * @param memberAuthDto 인증된 사용자 정보
	 * @param feedId 삭제할 게시글 ID
	 * @return 삭제된 게시글 ID를 포함한 응답 DTO
	 * @author 기원
	 */
	@Operation(summary = "게시글 삭제(feedId 기반 / soft delete)", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/{feedId}")
	public ResponseEntity<CommonResponse<FeedDeleteResponse>> deleteFeed(
		@AuthenticationPrincipal MemberAuth memberAuthDto,
		@PathVariable Long feedId) {
		FeedDeleteResponse response = feedService.deleteFeed(memberAuthDto.getId(), feedId);
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_DELETED, response));
	}

	/**
	 * 커서 기반 좋아요한 게시글 다건 조회
	 *
	 * @param memberAuth 인증된 사용자 정보
	 * @param req 커서 및 페이지 크기 요청 DTO
	 * @return 좋아요한 게시글 목록 응답
	 * @author 기원
	 */
	@Operation(summary = "좋아요누른 게시글 조회", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/liked")
	public ResponseEntity<CommonResponse<CursorPageResponse<FeedResponseDto>>> getLikedFeed(
		@AuthenticationPrincipal MemberAuth memberAuth,
		@ModelAttribute CursorPageRequest req
	) {
		CursorPageResponse<FeedResponseDto> response = feedService.getLikedFeedByCursor(req, memberAuth.getId());
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_LISTED_LIKE, response));
	}

	/**
	 * 게시글 조회(기간 + 정렬 + 커서)
	 * 		기간 필터(startDate~endDate 또는 weeks/months)
	 * 		정렬 조건(sort: latest, likes, comments)
	 * 		커서 기반 페이징(cursor, size)
	 * @param feedSearchCondition 사용자가 입력한 검색 조건 (기간 + 정렬 + 커서 정보 포함)
	 * @return 검색 결과: 페이징된 게시글 목록 DTO
	 * @author 기원
	 */
	@Operation(summary = "게시글 조회(기간 + 정렬 + 커서)", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/search")
	public ResponseEntity<CommonResponse<CursorPageResponse<FeedResponseDto>>> searchFeeds(
		@Valid @ModelAttribute FeedSearchCondition feedSearchCondition
	) {
		feedSearchCondition.feedSearch();
		CursorPageResponse<FeedResponseDto> response = feedService.searchFeeds(feedSearchCondition);
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_LISTED, response));
	}
}
