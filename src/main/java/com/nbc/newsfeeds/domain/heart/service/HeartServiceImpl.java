package com.nbc.newsfeeds.domain.heart.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nbc.newsfeeds.domain.heart.dto.HeartResponseDto;
import com.nbc.newsfeeds.domain.heart.entity.Heart;
import com.nbc.newsfeeds.domain.heart.repository.HeartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService {

	private final HeartRepository heartRepository;

	private boolean isUserAuthorized(Long memberId) {
		// 현재는 항상 true를 반환하는 스텁입니다.
		// FIXME: 실제 사용자의 권한을 확인하는 로직으로 대체해야 합니다.
		return true;
	}

	@Transactional
	public void addHeart(long memberId, long feedId) {
		// FIXME: 실제 인가 로직을 구현해야 합니다.
		// 현재는 간단한 구현으로, 실제 권한 검증 로직을 추후에 추가할 예정입니다.
		if (!isUserAuthorized(memberId)) {
			throw new AccessDeniedException("해당 사용자에게 좋아요 추가 권한이 없습니다.");
		}
		Optional<Heart> heartOptional = heartRepository.findByMemberIdAndFeedId(memberId, feedId);
		if(!heartOptional.isPresent()) {
			// FIXME: 뉴스피드 테이블의 heart_count 를 더하는 로직을 구현해야합니다.
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 사용자가 해당 피드에 좋아요를 이미 남겼습니다."));
		}

	}

	@Transactional
	public void cancelHeart(long memberId, long feedId) {
		// FIXME: 실제 인가 로직을 구현해야 합니다.
		// 현재는 간단한 구현으로, 실제 권한 검증 로직을 추후에 추가할 예정입니다.
		if (!isUserAuthorized(memberId)) {
			throw new AccessDeniedException("해당 사용자에게 좋아요 취소 권한이 없습니다.");
		}
		heartRepository.findByMemberIdAndFeedId(memberId, feedId).orElseThrow(() -> new ResponseStatusException(
			HttpStatus.BAD_REQUEST, "해당 사용자가 해당 피드에 좋아요를 남긴 기록이 없습니다."));

		// FIXME: 뉴스피드 테이블의 heart_count 를 취소하는 로직을 구현해야합니다.
	}

	@Transactional(readOnly = true)
	public HeartResponseDto viewHeart(long memberId, long feedId) {
		// FIXME: 뉴스피드 테이블의 heart_count 를 읽어오는 로직이 구현되야합니다.
	}

}
