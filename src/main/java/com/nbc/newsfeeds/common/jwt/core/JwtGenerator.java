package com.nbc.newsfeeds.common.jwt.core;

import java.util.Date;

import javax.crypto.SecretKey;

import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.common.jwt.constant.TokenExpiredConstant;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtGenerator {
	private final SecretKey secretKey;
	private final TokenExpiredConstant tokenExpiredConstant;

	public String generateAccessToken(MemberAuth memberAuth, Date date) {
		return buildToken(memberAuth, JwtConstants.ACCESS_TOKEN, date,
			tokenExpiredConstant.getAccessTokenExpiredDate(date));
	}

	public String generateRefreshToken(MemberAuth memberAuth, Date date) {
		return buildToken(memberAuth, JwtConstants.REFRESH_TOKEN, date,
			tokenExpiredConstant.getRefreshTokenExpiredDate(date));
	}

	private String buildToken(MemberAuth memberAuth, String tokenType, Date date, Date tokenExpiredConstant) {
		return Jwts.builder()
			.subject(memberAuth.getEmail())
			.id(memberAuth.getId().toString())
			.claim(JwtConstants.TOKEN_TYPE, tokenType)
			.claim(JwtConstants.KEY_ROLES, memberAuth.getRoles())
			.issuedAt(date)
			.expiration(tokenExpiredConstant)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}
}
