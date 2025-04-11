package com.nbc.newsfeeds.common.redis.service;

import org.springframework.stereotype.Service;

import com.nbc.newsfeeds.common.redis.dto.TokenDto;
import com.nbc.newsfeeds.common.redis.exception.RedisException;
import com.nbc.newsfeeds.common.redis.exception.RedisTokenResponseCode;
import com.nbc.newsfeeds.common.redis.repository.AccessTokenBlackListRepository;
import com.nbc.newsfeeds.common.redis.repository.RefreshTokenRepository;
import com.nbc.newsfeeds.common.redis.vo.AccessTokenBlackList;
import com.nbc.newsfeeds.common.redis.vo.RefreshToken;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final AccessTokenBlackListRepository accessTokenBlackListRepository;

	/**
	 * refresh token 저장
	 * @param refreshTokenDto 이메일, refresh token, 만료 시간
	 */
	@Transactional
	public void saveRefreshToken(TokenDto refreshTokenDto) {
		refreshTokenRepository.save(RefreshToken.builder()
			.email(refreshTokenDto.getEmail())
			.refreshToken(refreshTokenDto.getToken())
			.expiration(refreshTokenDto.getTimeToLive())
			.build());
	}

	/**
	 * refresh token 삭제
	 * 재 로그인 시 저장된 refresh token 삭제
	 * @param email 이메일
	 */
	@Transactional
	public void deleteRefreshToken(String email) {
		refreshTokenRepository.findById(email)
			.ifPresent(refreshTokenRepository::delete);
	}

	/**
	 * black list 추가
	 * 로그아웃 시 해당 access token으로 접근하지 못하도록 black list 등록
	 * @param tokenDto 이메일, access token, 만료시간
	 */
	@Transactional
	public void saveAccessTokenBlackList(TokenDto tokenDto) {
		accessTokenBlackListRepository.save(AccessTokenBlackList.builder()
			.accessToken(tokenDto.getToken())
			.expiration(tokenDto.getTimeToLive())
			.build());
	}

	public String getRefreshToken(String email) {
		return refreshTokenRepository.findById(email)
			.orElseThrow(() -> new RedisException(RedisTokenResponseCode.NOT_FOUND))
			.getRefreshToken();
	}

	public boolean isBlackListed(String token) {
		return accessTokenBlackListRepository.existsById(token);
	}
}
