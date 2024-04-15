package com.example.dreamvalutbackend.config.jwt.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.example.dreamvalutbackend.config.exception.CustomExpiredJwtException;
import com.example.dreamvalutbackend.config.exception.CustomJwtException;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;

public class JwtUtils {

	public static String secretKey = JwtConstants.key;

	// 헤더에 "Bearer XXX" 형식으로 담겨온 토큰을 추출한다
	public static String getTokenFromHeader(String header) {
		return header.split(" ")[1];
	}

	public static String generateToken(Map<String, Object> valueMap, int validTime) {
		SecretKey key = null;
		try {
			key = Keys.hmacShaKeyFor(JwtUtils.secretKey.getBytes(StandardCharsets.UTF_8));
		} catch(Exception e){
			throw new RuntimeException(e.getMessage());
		}
		return Jwts.builder()
			.setHeader(Map.of("typ","JWT"))
			.setClaims(valueMap)
			.setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
			.setExpiration(Date.from(ZonedDateTime.now().plusMinutes(validTime).toInstant()))
			.signWith(key)
			.compact();
	}

	public static Authentication getAuthentication(String token) {
		Map<String, Object> claims = validateToken(token);

		String email = (String) claims.get("email");
		String name = (String) claims.get("name");
		String role = (String) claims.get("role");
		String userId = (String) claims.get("userId");
		UserRole userRole = UserRole.valueOf(role);

		User user = User.builder().userEmail(email).userName(name).role(userRole).build();
		Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole().getValue()));
		UserDetailPrincipal principalDetail = new UserDetailPrincipal(user, authorities);

		return new UsernamePasswordAuthenticationToken(principalDetail, "", authorities);
	}

	public static Map<String, Object> validateToken(String token) {
		Map<String, Object> claim = null;
		try {
			SecretKey key = Keys.hmacShaKeyFor(JwtUtils.secretKey.getBytes(StandardCharsets.UTF_8));
			claim = Jwts.parser()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
				.getBody();
		} catch(ExpiredJwtException expiredJwtException){
			throw new CustomExpiredJwtException("토큰이 만료되었습니다", expiredJwtException);
		} catch(Exception e){
			throw new CustomJwtException("Error");
		}
		return claim;
	}

	// 토큰이 만료되었는지 판단하는 메서드
	public static boolean isExpired(String token) {
		try {
			validateToken(token);
		} catch (Exception e) {
			return (e instanceof CustomExpiredJwtException);
		}
		return false;
	}

	// 토큰의 남은 만료시간 계산
	public static long tokenRemainTime(Integer expTime) {
		Date expDate = new Date((long) expTime * (1000));
		long remainMs = expDate.getTime() - System.currentTimeMillis();
		return remainMs / (1000 * 60);
	}
}
