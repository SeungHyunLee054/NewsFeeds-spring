package com.nbc.newsfeeds.domain.feed.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.domain.feed.code.FeedSuccessCode;
import com.nbc.newsfeeds.domain.feed.dto.FeedDeleteResponse;
import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;
import com.nbc.newsfeeds.domain.feed.service.FeedService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

	private final FeedService feedService;

	@Operation(summary = "게시글 작성", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping
	public ResponseEntity<CommonResponse<FeedResponseDto>> createFeed(@AuthenticationPrincipal MemberAuthDto memberAuthDto, @Valid @RequestBody FeedRequestDto requestDto){
		FeedResponseDto response = feedService.createFeed(memberAuthDto.getId(), requestDto);
		return  ResponseEntity.status(FeedSuccessCode.FEED_CREATED.getHttpStatus()).body(CommonResponse.of(FeedSuccessCode.FEED_CREATED, response));
	}

	@Operation(summary = "게시글 단건 조회(feedId 기반)", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/{feedId}")
	public ResponseEntity<CommonResponse<FeedResponseDto>> getFeed(@PathVariable Long feedId) {
		FeedResponseDto responseDto = feedService.getFeedById(feedId);
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_FOUND, responseDto));
	}

	@Operation(summary = "게시글 목록 조회(커서 기반)", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping
	public ResponseEntity<CommonResponse<CursorPageResponse<FeedResponseDto>>> getFeeds(@Valid @ModelAttribute CursorPageRequest req){
		CursorPageResponse<FeedResponseDto> response = feedService.getFeedByCursor(req);
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_LISTED, response));
	}

	@Operation(summary = "게시글 수정(feedId 기반)", security = {@SecurityRequirement(name = "bearer-key")})
	@PutMapping("/{feedId}")
	public ResponseEntity<CommonResponse<FeedResponseDto>> updateFeed(@AuthenticationPrincipal MemberAuthDto memberAuthDto, @PathVariable Long feedId, @Valid @RequestBody FeedRequestDto requestDto){
		FeedResponseDto responseDto = feedService.updateFeed(memberAuthDto.getId(), feedId, requestDto);
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_UPDATED, responseDto));
	}

	@Operation(summary = "게시글 삭제(feedId 기반 / soft delete)", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/{feedId}")
	public ResponseEntity<CommonResponse<FeedDeleteResponse>> deleteFeed(@AuthenticationPrincipal MemberAuthDto memberAuthDto, @PathVariable Long feedId){
		feedService.deleteFeed(memberAuthDto.getId(), feedId);
		return ResponseEntity.ok(CommonResponse.of(FeedSuccessCode.FEED_DELETED, new FeedDeleteResponse(feedId)));
	}
}
