package com.nbc.newsfeeds.common.jwt.core;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.common.jwt.constant.TokenExpiredConstant;
import com.nbc.newsfeeds.common.jwt.dto.TokensDto;
import com.nbc.newsfeeds.common.redis.dto.TokenDto;
import com.nbc.newsfeeds.common.redis.service.RedisService;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
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

	public TokensDto issueToken(MemberAuthDto memberAuthDto, Date date) {
		String accessToken = jwtGenerator.generateAccessToken(memberAuthDto, date);
		String refreshToken = jwtGenerator.generateRefreshToken(memberAuthDto, date);

		redisService.deleteRefreshToken(memberAuthDto.getEmail());

		redisService.saveRefreshToken(TokenDto.builder()
			.email(memberAuthDto.getEmail())
			.token(refreshToken)
			.timeToLive(tokenExpiredConstant.getRefreshTokenExpiredMinute())
			.build());

		return new TokensDto(accessToken, refreshToken);
	}

	public String regenerateAccessToken(String refreshToken) {
		if (jwtParser.isTokenExpired(refreshToken)) {
			throw new JwtException("refresh token이 만료되었습니다.");
		}

		MemberAuthDto memberAuthDto = jwtParser.getMemberAuthDto(refreshToken);
		String savedToken = redisService.getRefreshToken(memberAuthDto.getEmail());

		if (!savedToken.equals(refreshToken)) {
			throw new JwtException("유저의 refresh token이 아닙니다.");
		}

		return jwtGenerator.generateAccessToken(memberAuthDto, new Date());
	}

	public void blockAccessToken(String accessToken, MemberAuthDto memberAuthDto) {
		redisService.saveAccessTokenBlackList(TokenDto.builder()
			.email(memberAuthDto.getEmail())
			.token(accessToken)
			.timeToLive(tokenExpiredConstant.getAccessTokenExpiredMinute())
			.build());
	}

	public Authentication getAuthentication(String token) {
		MemberAuthDto memberAuthDto = jwtParser.getMemberAuthDto(token);

		return new UsernamePasswordAuthenticationToken(memberAuthDto, token, memberAuthDto.getAuthorities());
	}

	public boolean isBlackListed(String token) {
		return redisService.isBlackListed(token);
	}

	public boolean isTokenExpired(String token) {
		return jwtParser.isTokenExpired(token);
	}

	public String getTokenTypeFromToken(String token) {
		return jwtParser.getTokenTypeFromToken(token);
	}
}
