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

	/**
	 * token 생성<br>
	 * 타입을 통해 refresh token인지 access token인지 구분, 유저 정보와 만료 기간이 설정
	 * @param memberAuth 유저 정보
	 * @param tokenType 토큰 타입
	 * @param date 로그인 시간
	 * @param tokenExpiredConstant 만료 시간
	 * @return token
	 */
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
