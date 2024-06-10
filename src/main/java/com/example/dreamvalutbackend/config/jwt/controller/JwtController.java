package com.example.dreamvalutbackend.config.jwt.controller;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.dreamvalutbackend.config.exception.CustomJwtException;
import com.example.dreamvalutbackend.config.jwt.utils.JwtConstants;
import com.example.dreamvalutbackend.config.jwt.utils.JwtUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwtController {

	private final StringRedisTemplate stringRedisTemplate;

	@PostMapping("/refresh")
	public ResponseEntity<Map<String, Object>> refresh(@RequestHeader("Authorization") String authHeader, @RequestHeader("X-Refresh-Token") String refreshToken) {
		if (authHeader == null || !authHeader.startsWith(JwtConstants.JWT_TYPE)) {
			throw new CustomJwtException("Access Token 형식이 올바르지 않습니다.");
		}

		String accessToken = JwtUtils.getTokenFromHeader(authHeader);

		if (!JwtUtils.isExpired(accessToken)) {
			return ResponseEntity.ok(Map.of("accessToken", accessToken, "refreshToken", refreshToken));
		}

		Map<String, Object> claims = JwtUtils.validateToken(refreshToken);
		String userId = (String) claims.get("userId");
		// #TODO: Redis 토큰 형식 오류 수정
		// String storedToken = null;
		// try {
		// 	storedToken = stringRedisTemplate.opsForValue().get("refreshToken:" + userId);
		// 	if (!refreshToken.equals(storedToken)) {
		// 		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
		// 	}
		// } catch (Exception e) {
		// 	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Redis access error", "details" , e.getMessage()));
		// }


		String newAccessToken = JwtUtils.generateToken(claims, JwtConstants.ACCESS_EXP_TIME);
		String newRefreshToken = refreshToken;
		// long expTime = JwtUtils.tokenRemainTime((Integer) claims.get("exp"));
		long expTime = ((Number) claims.get("exp")).longValue();
		if (expTime <= 60) {
			newRefreshToken = JwtUtils.generateToken(claims, JwtConstants.REFRESH_EXP_TIME);
		}

		return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken));
	}


	@PostMapping("/signout")
	public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String authHeader, @RequestHeader("X-Refresh-Token") String refreshToken) {
		if (authHeader == null || !authHeader.startsWith(JwtConstants.JWT_TYPE)) {
			throw new CustomJwtException("Access Token 형식이 올바르지 않습니다.");
		}

		String accessToken = JwtUtils.getTokenFromHeader(authHeader);
		Map<String, Object> claims = JwtUtils.validateToken(accessToken);
		String userId = (String) claims.get("userId");

		stringRedisTemplate.delete("refreshToken:" + userId);

		return ResponseEntity.noContent().build();
	}
}
