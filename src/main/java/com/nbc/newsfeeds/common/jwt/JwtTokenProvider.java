package com.nbc.newsfeeds.common.jwt;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.nbc.newsfeeds.common.jwt.constant.JwtConstants;
import com.nbc.newsfeeds.common.jwt.constant.TokenExpiredConstant;
import com.nbc.newsfeeds.common.jwt.dto.TokenDto;
import com.nbc.newsfeeds.domain.member.dto.MemberAuthDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtTokenProvider {
	private final SecretKey accessSecretKey;
	private final TokenExpiredConstant tokenExpiredConstant;

	public JwtTokenProvider(@Value("${spring.jwt.secret}") String accessSecretKey,
		TokenExpiredConstant tokenExpiredConstant) {
		this.accessSecretKey = Keys.hmacShaKeyFor(accessSecretKey.getBytes());
		this.tokenExpiredConstant = tokenExpiredConstant;
	}

	public String generateAccessToken(MemberAuthDto memberAuthDto, Date date) {
		return Jwts.builder()
			.subject(memberAuthDto.getEmail())
			.id(memberAuthDto.getId().toString())
			.claim(JwtConstants.TOKEN_TYPE, JwtConstants.ACCESS_TOKEN)
			.claim("roles", List.of("ROLE_USER"))
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
			.issuedAt(date)
			.expiration(tokenExpiredConstant.getRefreshTokenExpiredDate(date))
			.signWith(accessSecretKey, Jwts.SIG.HS256)
			.compact();
	}

	public TokenDto getToken(MemberAuthDto memberAuthDto, Date date) {
		String accessToken = generateAccessToken(memberAuthDto, date);
		String refreshToken = generateRefreshToken(memberAuthDto, date);

		return new TokenDto(accessToken, refreshToken);
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

	private MemberAuthDto getMemberAuthDto(String token) {
		Claims claims = parseToken(token);

		return MemberAuthDto.builder()
			.id(Long.valueOf(claims.getId()))
			.email(claims.getSubject())
			.build();
	}

	private Claims parseToken(String token) {
		try {
			return Jwts.parser().verifyWith(accessSecretKey).build().parseSignedClaims(token).getPayload();
		} catch (ExpiredJwtException expiredJwtException) {
			return expiredJwtException.getClaims();
		} catch (MalformedJwtException malformedJwtException) {
			throw new MalformedJwtException("not a valid JWT token");
		} catch (SignatureException signatureException) {
			throw new SignatureException("not a valid JWT token");
		} catch (UnsupportedJwtException unsupportedJwtException) {
			throw new UnsupportedJwtException("not a valid JWT token");
		}
	}

}
