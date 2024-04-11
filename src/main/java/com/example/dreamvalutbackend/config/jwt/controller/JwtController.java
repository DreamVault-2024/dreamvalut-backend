package com.example.dreamvalutbackend.config.jwt.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dreamvalutbackend.config.exception.CustomJwtException;
import com.example.dreamvalutbackend.config.jwt.utils.JwtConstants;
import com.example.dreamvalutbackend.config.jwt.utils.JwtUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwtController {
	@RequestMapping("/refresh")
	public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken) {
		if (authHeader == null) {
			throw new CustomJwtException("Access Token 이 존재하지 않습니다");
		} else if (!authHeader.startsWith(JwtConstants.JWT_TYPE)) {
			throw new CustomJwtException("BEARER 로 시작하지 않는 올바르지 않은 토큰 형식입니다");
		}

		String accessToken = JwtUtils.getTokenFromHeader(authHeader);

		if (!JwtUtils.isExpired(accessToken)) {
			return Map.of("Access Token", accessToken, "Refresh Token", refreshToken);
		}

		Map<String, Object> claims = JwtUtils.validateToken(refreshToken);
		String newAccessToken = JwtUtils.generateToken(claims, JwtConstants.ACCESS_EXP_TIME);

		String newRefreshToken = refreshToken;
		long expTime = JwtUtils.tokenRemainTime((Integer) claims.get("exp"));
		if (expTime <= 60) {
			newRefreshToken = JwtUtils.generateToken(claims, JwtConstants.REFRESH_EXP_TIME);
		}

		return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
	}
}
