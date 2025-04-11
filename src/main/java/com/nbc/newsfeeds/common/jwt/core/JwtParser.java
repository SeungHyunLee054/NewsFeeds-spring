package com.nbc.newsfeeds.common.jwt.core;

import java.util.Date;

import javax.crypto.SecretKey;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.common.jwt.exception.JwtTokenException;
import com.nbc.newsfeeds.common.jwt.exception.JwtTokenExceptionCode;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;

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

	/**
	 * 토큰 parsing<br>
	 * 토큰에 저장된 claims를 parsing하여 사용 가능
	 * @param token token
	 * @return claims
	 */
	private Claims parseToken(String token) {
		try {
			return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
		} catch (ExpiredJwtException expiredJwtException) {
			return expiredJwtException.getClaims();
		} catch (MalformedJwtException malformedJwtException) {
			throw new JwtTokenException(JwtTokenExceptionCode.NOT_VALID_JWT_TOKEN);
		} catch (SignatureException signatureException) {
			throw new JwtTokenException(JwtTokenExceptionCode.NOT_VALID_SIGNATURE);
		} catch (UnsupportedJwtException unsupportedJwtException) {
			throw new JwtTokenException(JwtTokenExceptionCode.NOT_VALID_CONTENT);
		}
	}

	public boolean isTokenExpired(String token) {
		Claims claims = parseToken(token);
		return claims.getExpiration().before(new Date());
	}

	public MemberAuth getMemberAuthDto(String token) {
		Claims claims = parseToken(token);

		return MemberAuth.builder()
			.id(Long.valueOf(claims.getId()))
			.email(claims.getSubject())
			.roles(objectMapper.convertValue(claims.get(JwtConstants.KEY_ROLES), new TypeReference<>() {
			}))
			.build();
	}
}
