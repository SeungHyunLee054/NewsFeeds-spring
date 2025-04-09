package com.nbc.newsfeeds.domain.feed.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.nbc.newsfeeds.common.request.CursorPageRequest;
import com.nbc.newsfeeds.common.response.CursorPageResponse;
import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;
import com.nbc.newsfeeds.domain.feed.service.FeedService;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

	private final FeedService feedService;

	@PostMapping
	public ResponseEntity<FeedResponseDto> createFeed(@AuthenticationPrincipal MemberAuthDto memberAuthDto, @Valid @RequestBody FeedRequestDto requestDto){
		FeedResponseDto response = feedService.createFeed(memberAuthDto.getId(), requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{feedId}")
	public ResponseEntity<FeedResponseDto> getFeed(@PathVariable Long feedId){
		FeedResponseDto responseDto = feedService.getFeedById(feedId);
		return ResponseEntity.ok(responseDto);
	}

	@Operation(summary = "게시글 목록 조회(커서 기반)", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping
	public ResponseEntity<CursorPageResponse<FeedResponseDto>> getFeeds(@Valid @ModelAttribute CursorPageRequest req) {
		CursorPageResponse<FeedResponseDto> response = feedService.getFeedByCursor(req);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{feedId}")
	public ResponseEntity<FeedResponseDto> updateFeed(
		@AuthenticationPrincipal MemberAuthDto memberAuthDto,
		@PathVariable Long feedId,
		@RequestBody FeedRequestDto requestDto
	) {
		FeedResponseDto responseDto = feedService.updateFeed(memberAuthDto.getId(), feedId, requestDto);
		return ResponseEntity.ok(responseDto);
	}

	@DeleteMapping("/{feedId}")
	public ResponseEntity<Void> deleteFeed(
		@AuthenticationPrincipal MemberAuthDto memberAuthDto,
		@PathVariable Long feedId
	) {
		feedService.deleteFeed(memberAuthDto.getId(), feedId);
		return ResponseEntity.ok().build();
	}
}
