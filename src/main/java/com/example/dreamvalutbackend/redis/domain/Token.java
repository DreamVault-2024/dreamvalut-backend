package com.example.dreamvalutbackend.redis.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 86400)
public class Token {
	@Id
	private String userId;

	private String refreshToken;

	public Token(String userId, String refreshToken) {
		this.userId = userId;
		this.refreshToken = refreshToken;
	}
}
