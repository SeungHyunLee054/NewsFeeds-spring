package com.nbc.newsfeeds.domain.feed.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;
import com.nbc.newsfeeds.domain.feed.service.FeedService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {

	private final FeedService feedService;

	@PostMapping
	public ResponseEntity<FeedResponseDto> createFeed(@Valid @RequestBody FeedRequestDto requestDto){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userEmail = authentication.getName();

		return ResponseEntity.status(HttpStatus.CREATED).body(feedService.createFeed(requestDto, userEmail));
	}

	@GetMapping("/{feedId}")
	public ResponseEntity<FeedResponseDto> getFeed(@PathVariable Long feedId){
		FeedResponseDto responseDto = feedService.getFeedById(feedId);
		return ResponseEntity.ok(responseDto);
	}

	@GetMapping
	public ResponseEntity<Page<FeedResponseDto>> getFeedAll(@PageableDefault(size = 10) Pageable pageable){
		Page<FeedResponseDto> responseDto = feedService.getAllFeed(pageable);
		return ResponseEntity.ok(responseDto);
	}

	@PutMapping("/{feedId}")
	public ResponseEntity<FeedResponseDto> updateFeed(@PathVariable Long feedId, @RequestBody FeedRequestDto requestDto){
		FeedResponseDto responseDto = feedService.updateFeed(feedId, requestDto);
		return ResponseEntity.ok(responseDto);
	}

	@DeleteMapping("/{feedId}")
	public ResponseEntity<Void> deleteFeed(@PathVariable Long feedId){
		feedService.deleteFeed(feedId);
		return ResponseEntity.ok().build();
	}

}
