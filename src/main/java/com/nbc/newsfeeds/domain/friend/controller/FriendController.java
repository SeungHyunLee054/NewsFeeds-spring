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

import com.nbc.newsfeeds.domain.friend.model.request.FindFriendsRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RequestFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.request.RespondToFriendRequest;
import com.nbc.newsfeeds.domain.friend.model.response.FindFriendsResponse;
import com.nbc.newsfeeds.domain.friend.service.FriendService;

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

	@Operation(summary = "친구 요청", security = {@SecurityRequirement(name = "Bearer")})
	@PostMapping("/request")
	public ResponseEntity<Void> requestFriend(
		@AuthenticationPrincipal Long memberId,
		@Valid @RequestBody RequestFriendRequest req
	) {
		friendService.requestFriend(memberId, req);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "친구 요청 응답", security = {@SecurityRequirement(name = "Bearer")})
	@PatchMapping("/request/{friendshipId}")
	public ResponseEntity<Void> respondToFriendRequest(
		@AuthenticationPrincipal Long memberId,
		@PathVariable Long friendshipId,
		@Valid @RequestBody RespondToFriendRequest req
	) {
		friendService.respondToFriendRequest(memberId, friendshipId, req);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "친구 삭제", security = {@SecurityRequirement(name = "Bearer")})
	@DeleteMapping("/{friendshipId}")
	public ResponseEntity<Void> deleteFriend(
		@AuthenticationPrincipal Long memberId,
		@PathVariable Long friendshipId
	) {
		friendService.deleteFriend(memberId, friendshipId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "친구 목록 조회", security = {@SecurityRequirement(name = "Bearer")})
	@GetMapping
	public ResponseEntity<FindFriendsResponse> findFriends(
		@AuthenticationPrincipal Long memberId,
		@Valid @ModelAttribute FindFriendsRequest req
	) {
		FindFriendsResponse res = friendService.findFriends(memberId, req);
		return ResponseEntity.ok(res);
	}

}
