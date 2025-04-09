package com.nbc.newsfeeds.common.jwt.core;

import java.util.Date;

import javax.crypto.SecretKey;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtParser {
	private final SecretKey secretKey;
	private final ObjectMapper objectMapper;

	private Claims parseToken(String token) {
		try {
			return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
		} catch (ExpiredJwtException expiredJwtException) {
			return expiredJwtException.getClaims();
		} catch (MalformedJwtException malformedJwtException) {
			throw new MalformedJwtException("옳바르지 않은 JWT 토큰입니다.");
		} catch (SignatureException signatureException) {
			throw new SignatureException("서명이 옳바르지 않습니다.");
		} catch (UnsupportedJwtException unsupportedJwtException) {
			throw new UnsupportedJwtException("내용이 옳바르지 않습니다.");
		}
	}

	public boolean isTokenExpired(String token) {
		Claims claims = parseToken(token);
		return claims.getExpiration().before(new Date());
	}

	public String getTokenTypeFromToken(String token) {
		Claims claims = parseToken(token);

		return claims.get(JwtConstants.TOKEN_TYPE,String.class);
	}

	public MemberAuthDto getMemberAuthDto(String token) {
		Claims claims = parseToken(token);

		return MemberAuthDto.builder()
			.id(Long.valueOf(claims.getId()))
			.email(claims.getSubject())
			.roles(objectMapper.convertValue(claims.get(JwtConstants.KEY_ROLES), new TypeReference<>() {
			}))
			.build();
	}
}
