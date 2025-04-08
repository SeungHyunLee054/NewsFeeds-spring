package com.nbc.newsfeeds.common.jwt;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.common.jwt.constant.TokenExpiredConstant;
import com.nbc.newsfeeds.common.jwt.dto.TokensDto;
import com.nbc.newsfeeds.common.redis.dto.TokenDto;
import com.nbc.newsfeeds.common.redis.service.RedisService;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtTokenProvider {
	private final SecretKey accessSecretKey;
	private final TokenExpiredConstant tokenExpiredConstant;
	private final RedisService redisService;
	private final ObjectMapper objectMapper;

	public JwtTokenProvider(@Value("${spring.jwt.secret}") String accessSecretKey,
		TokenExpiredConstant tokenExpiredConstant, RedisService redisService, ObjectMapper objectMapper) {
		this.accessSecretKey = Keys.hmacShaKeyFor(accessSecretKey.getBytes());
		this.tokenExpiredConstant = tokenExpiredConstant;
		this.redisService = redisService;
		this.objectMapper = objectMapper;
	}

	public String generateAccessToken(MemberAuthDto memberAuthDto, Date date) {
		return Jwts.builder()
			.subject(memberAuthDto.getEmail())
			.id(memberAuthDto.getId().toString())
			.claim(JwtConstants.TOKEN_TYPE, JwtConstants.ACCESS_TOKEN)
			.claim(JwtConstants.KEY_ROLES, memberAuthDto.getRoles())
			.issuedAt(date)
			.expiration(tokenExpiredConstant.getAccessTokenExpiredDate(date))
			.signWith(accessSecretKey, Jwts.SIG.HS256)
			.compact();
	}

	public String generateRefreshToken(MemberAuthDto memberAuthDto, Date date) {
		return Jwts.builder()
			.subject(memberAuthDto.getEmail())
			.id(memberAuthDto.getId().toString())
			.claim(JwtConstants.TOKEN_TYPE, JwtConstants.REFRESH_TOKEN)
			.claim(JwtConstants.KEY_ROLES, memberAuthDto.getRoles())
			.issuedAt(date)
			.expiration(tokenExpiredConstant.getRefreshTokenExpiredDate(date))
			.signWith(accessSecretKey, Jwts.SIG.HS256)
			.compact();
	}

	public TokensDto getToken(MemberAuthDto memberAuthDto, Date date) {
		String accessToken = generateAccessToken(memberAuthDto, date);
		String refreshToken = generateRefreshToken(memberAuthDto, date);

		redisService.deleteRefreshToken(memberAuthDto.getEmail());

		redisService.saveRefreshToken(TokenDto.builder()
			.email(memberAuthDto.getEmail())
			.token(refreshToken)
			.timeToLive(tokenExpiredConstant.getRefreshTokenExpiredMinute())
			.build());

		return new TokensDto(accessToken, refreshToken);
	}

	public String generateAccessTokenByRefreshToken(String refreshToken) {
		if (isTokenExpired(refreshToken)) {
			throw new JwtException("refresh token이 만료되었습니다.");
		}

		String emailFromToken = getEmailFromToken(refreshToken);
		long memberIdFromToken = getMemberIdFromToken(refreshToken);

		String savedToken = redisService.getRefreshToken(emailFromToken);
		if (!savedToken.equals(refreshToken)) {
			throw new JwtException("유저의 refresh token이 아닙니다.");
		}

		List<String> rolesFromToken = getRolesFromToken(refreshToken);

		return generateAccessToken(MemberAuthDto.builder()
			.id(memberIdFromToken)
			.email(emailFromToken)
			.roles(rolesFromToken)
			.build(), new Date());
	}

	public void BlockAccessToken(String accessToken, MemberAuthDto memberAuthDto) {
		redisService.saveAccessTokenBlackList(TokenDto.builder()
			.email(memberAuthDto.getEmail())
			.token(accessToken)
			.timeToLive(tokenExpiredConstant.getAccessTokenExpiredMinute())
			.build());
	}

	public Authentication getAuthentication(String token) {
		MemberAuthDto memberAuthDto = getMemberAuthDto(token);

		List<SimpleGrantedAuthority> grantedAuthorities = memberAuthDto.getRoles().stream()
			.map(SimpleGrantedAuthority::new).toList();

		return new UsernamePasswordAuthenticationToken(memberAuthDto, token, grantedAuthorities);
	}

	public boolean isTokenExpired(String token) {
		Claims claims = parseToken(token);
		return claims.getExpiration().before(new Date());
	}

	public boolean isBlackListed(String token) {
		return redisService.isBlackListed(token);
	}

	public String getEmailFromToken(String token) {
		Claims claims = parseToken(token);

		return claims.getSubject();
	}

	public long getMemberIdFromToken(String token) {
		Claims claims = parseToken(token);

		return Long.parseLong(claims.getId());
	}

	public List<String> getRolesFromToken(String token) {
		Claims claims = parseToken(token);

		claims.get(JwtConstants.KEY_ROLES,List.class);

		return objectMapper.convertValue(claims.get(JwtConstants.KEY_ROLES), new TypeReference<>(){});
	}

	public String getTokenTypeFromToken(String token) {
		Claims claims = parseToken(token);

		return claims.get(JwtConstants.TOKEN_TYPE,String.class);
	}

	private MemberAuthDto getMemberAuthDto(String token) {
		Claims claims = parseToken(token);

		return MemberAuthDto.builder()
			.id(Long.valueOf(claims.getId()))
			.email(claims.getSubject())
			.roles(objectMapper.convertValue(claims.get(JwtConstants.KEY_ROLES), new TypeReference<>(){}))
			.build();
	}

	private Claims parseToken(String token) {
		try {
			return Jwts.parser().verifyWith(accessSecretKey).build().parseSignedClaims(token).getPayload();
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

}
