package com.example.dreamvalutbackend.config.jwt.controller;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dreamvalutbackend.config.exception.CustomJwtException;
import com.example.dreamvalutbackend.config.jwt.utils.JwtConstants;
import com.example.dreamvalutbackend.config.jwt.utils.JwtUtils;
import com.example.dreamvalutbackend.redis.config.RedisConfiguration;
import com.example.dreamvalutbackend.redis.repository.TokenRepository;

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
			return ResponseEntity.ok(Map.of("Access Token", accessToken, "Refresh Token", refreshToken));
		}

		Map<String, Object> claims = JwtUtils.validateToken(refreshToken);
		String userId = (String) claims.get("userId");
		String storedToken = stringRedisTemplate.opsForValue().get("refreshToken:" + userId);
		if (!refreshToken.equals(storedToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
		}


		String newAccessToken = JwtUtils.generateToken(claims, JwtConstants.ACCESS_EXP_TIME);
		String newRefreshToken = refreshToken;
		long expTime = JwtUtils.tokenRemainTime((Integer) claims.get("exp"));
		if (expTime <= 60) {
			newRefreshToken = JwtUtils.generateToken(claims, JwtConstants.REFRESH_EXP_TIME);
		}

		return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken));
	}
}
