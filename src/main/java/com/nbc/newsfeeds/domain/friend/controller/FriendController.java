package com.nbc.newsfeeds.domain.friend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbc.newsfeeds.domain.friend.model.request.CursorPageRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RequestFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RespondToFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.response.FriendRequestsResponse;
import com.nbc.newsfeeds.domain.friend.model.response.FriendsResponse;
import com.nbc.newsfeeds.domain.friend.service.FriendService;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "friend", description = "friend api")
@RequiredArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendController {

	private final FriendService friendService;

	@Operation(summary = "친구 요청", security = {@SecurityRequirement(name = "bearer-key")})
	@PostMapping("/requests")
	public ResponseEntity<Void> requestFriend(
		@AuthenticationPrincipal MemberAuthDto memberAuth,
		@Valid @RequestBody RequestFriendRequest req
	) {
		friendService.requestFriend(memberAuth.getId(), req);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "친구 요청 응답", security = {@SecurityRequirement(name = "bearer-key")})
	@PatchMapping("/requests/{friendshipId}")
	public ResponseEntity<Void> respondToFriendRequest(
		@AuthenticationPrincipal MemberAuthDto memberAuth,
		@PathVariable Long friendshipId,
		@Valid @RequestBody RespondToFriendRequest req
	) {
		friendService.respondToFriendRequest(memberAuth.getId(), friendshipId, req);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "친구 삭제", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/{friendshipId}")
	public ResponseEntity<Void> deleteFriend(
		@AuthenticationPrincipal MemberAuthDto memberAuth,
		@PathVariable Long friendshipId
	) {
		friendService.deleteFriend(memberAuth.getId(), friendshipId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "친구 목록 조회", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping
	public ResponseEntity<FriendsResponse> findFriends(
		@AuthenticationPrincipal MemberAuthDto memberAuth,
		@Valid @ModelAttribute CursorPageRequest req
	) {
		FriendsResponse res = friendService.findFriends(memberAuth.getId(), req);
		return ResponseEntity.ok(res);
	}

	@Operation(summary = "친구 요청 목록 조회", security = {@SecurityRequirement(name = "bearer-key")})
	@GetMapping("/requests")
	public ResponseEntity<FriendRequestsResponse> findFriendRequests(
		@AuthenticationPrincipal MemberAuthDto memberAuth,
		@Valid @ModelAttribute CursorPageRequest req
	) {
		FriendRequestsResponse res = friendService.findFriendRequests(memberAuth.getId(), req);
		return ResponseEntity.ok(res);
	}

	@Operation(summary = "친구 요청 취소", security = {@SecurityRequirement(name = "bearer-key")})
	@DeleteMapping("/requests/{friendshipId}")
	public ResponseEntity<Void> cancelFriendRequest(
		@AuthenticationPrincipal MemberAuthDto memberAuth,
		@PathVariable Long friendshipId
	) {
		friendService.cancelFriendRequest(memberAuth.getId(), friendshipId);
		return ResponseEntity.ok().build();
	}
}
