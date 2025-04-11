package com.nbc.newsfeeds.common.jwt.core;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.common.jwt.constant.TokenExpiredConstant;
import com.nbc.newsfeeds.common.jwt.dto.TokensDto;
import com.nbc.newsfeeds.common.jwt.exception.JwtTokenException;
import com.nbc.newsfeeds.common.jwt.exception.JwtTokenExceptionCode;
import com.nbc.newsfeeds.common.redis.dto.TokenDto;
import com.nbc.newsfeeds.common.redis.service.RedisService;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
	private static final int MILLI_SECONDS = 1000;

	private final JwtGenerator jwtGenerator;
	private final JwtParser jwtParser;
	private final RedisService redisService;
	private final TokenExpiredConstant tokenExpiredConstant;

	public JwtService(@Value("${spring.jwt.secret}") String secretKey, RedisService redisService,
		TokenExpiredConstant tokenExpiredConstant) {
		this.jwtGenerator = new JwtGenerator(Keys.hmacShaKeyFor(secretKey.getBytes()), tokenExpiredConstant);
		this.jwtParser = new JwtParser(Keys.hmacShaKeyFor(secretKey.getBytes()), new ObjectMapper());
		this.redisService = redisService;
		this.tokenExpiredConstant = tokenExpiredConstant;
	}

	public TokensDto issueToken(MemberAuth memberAuth, Date date) {
		String accessToken = jwtGenerator.generateAccessToken(memberAuth, date);
		String refreshToken = jwtGenerator.generateRefreshToken(memberAuth, date);

		redisService.deleteRefreshToken(memberAuth.getEmail());

		redisService.saveRefreshToken(TokenDto.builder()
			.email(memberAuth.getEmail())
			.token(refreshToken)
			.timeToLive(tokenExpiredConstant.getRefreshTokenExpiredMinute())
			.build());

		return new TokensDto(accessToken, refreshToken);
	}

	public String regenerateAccessToken(String refreshToken) {
		if (jwtParser.isTokenExpired(refreshToken)) {
			throw new JwtTokenException(JwtTokenExceptionCode.REFRESH_TOKEN_EXPIRED);
		}

		MemberAuth memberAuth = jwtParser.getMemberAuthDto(refreshToken);
		String savedToken = redisService.getRefreshToken(memberAuth.getEmail());

		if (!savedToken.equals(refreshToken)) {
			throw new JwtTokenException(JwtTokenExceptionCode.NOT_MATCHES_REFRESH_TOKEN);
		}

		return jwtGenerator.generateAccessToken(memberAuth, new Date());
	}

	public void blockAccessToken(String accessToken, MemberAuth memberAuth) {
		Date tokenExpiration = jwtParser.getTokenExpiration(accessToken);
		long now = System.currentTimeMillis();
		long timeToLive = (tokenExpiration.getTime() - now) / MILLI_SECONDS;

		if (timeToLive <= 0) {
			return;
		}

		redisService.saveAccessTokenBlackList(TokenDto.builder()
			.email(memberAuth.getEmail())
			.token(accessToken)
			.timeToLive(timeToLive)
			.build());
	}

	public MemberAuth getMemberAuth(String token) {
		return jwtParser.getMemberAuthDto(token);
	}

	public boolean isBlackListed(String token) {
		return redisService.isBlackListed(token);
	}

	public boolean isTokenExpired(String token) {
		return jwtParser.isTokenExpired(token);
	}
}
