package com.nbc.newsfeeds.common.jwt.core;

import java.util.Date;

import javax.crypto.SecretKey;

import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.common.jwt.constant.TokenExpiredConstant;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtGenerator {
	private final SecretKey secretKey;
	private final TokenExpiredConstant tokenExpiredConstant;

	public String generateAccessToken(MemberAuthDto memberAuthDto, Date date) {
		return buildToken(memberAuthDto, JwtConstants.ACCESS_TOKEN, date,
			tokenExpiredConstant.getAccessTokenExpiredDate(date));
	}

	public String generateRefreshToken(MemberAuthDto memberAuthDto, Date date) {
		return buildToken(memberAuthDto, JwtConstants.REFRESH_TOKEN, date,
			tokenExpiredConstant.getRefreshTokenExpiredDate(date));
	}

	private String buildToken(MemberAuthDto memberAuthDto, String tokenType, Date date, Date tokenExpiredConstant) {
		return Jwts.builder()
			.subject(memberAuthDto.getEmail())
			.id(memberAuthDto.getId().toString())
			.claim(JwtConstants.TOKEN_TYPE, tokenType)
			.claim(JwtConstants.KEY_ROLES, memberAuthDto.getRoles())
			.issuedAt(date)
			.expiration(tokenExpiredConstant)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}
}
