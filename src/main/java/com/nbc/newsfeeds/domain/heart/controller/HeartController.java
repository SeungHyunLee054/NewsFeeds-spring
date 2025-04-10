package com.nbc.newsfeeds.domain.heart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseCode;
import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;
import com.nbc.newsfeeds.domain.heart.service.HeartService;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feeds/{feedId}/likes")
@RequiredArgsConstructor
public class HeartController {

	private final HeartService heartService;

	@PostMapping
	@Operation(summary = "좋아요 추가", security = {@SecurityRequirement(name = "bearer-key")})
	public ResponseEntity<CommonResponse<HeartResponseDto>> addHeart(
		@AuthenticationPrincipal MemberAuth memberAuthDto,
		@PathVariable long feedId
	) {
		heartService.addHeart(memberAuthDto.getId(), feedId);
		return ResponseEntity
			.status(HeartResponseCode.HEART_CREATED.getHttpStatus())
			.body(CommonResponse.of(HeartResponseCode.HEART_CREATED, heartService.viewHeart(feedId)));
	}

	@DeleteMapping
	@Operation(summary = "좋아요 취소", security = {@SecurityRequirement(name = "bearer-key")})
	public ResponseEntity<CommonResponse<HeartResponseDto>> cancelHeart(
		@AuthenticationPrincipal MemberAuth memberAuthDto,
		@PathVariable long feedId
	) {
		heartService.cancelHeart(memberAuthDto.getId(), feedId);
		return ResponseEntity
			.status(HeartResponseCode.HEART_REMOVED.getHttpStatus())
			.body(CommonResponse.of(HeartResponseCode.HEART_REMOVED, heartService.viewHeart(feedId)));
	}

	@GetMapping
	@Operation(summary = "좋아요 확인")
	public ResponseEntity<CommonResponse<HeartResponseDto>> viewHeart(
		@PathVariable long feedId
	) {
		return ResponseEntity
			.status(HeartResponseCode.HEART_RETRIEVED.getHttpStatus())
			.body(CommonResponse.of(HeartResponseCode.HEART_REMOVED, heartService.viewHeart(feedId)));
	}
}
