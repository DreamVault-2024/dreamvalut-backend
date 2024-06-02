package com.example.dreamvalutbackend.domain.like.controller.response;

import com.example.dreamvalutbackend.domain.like.domain.Like;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeResponseDto {
	private Long id;
	private Long userId;
	private Long trackId;

	@Builder
	public LikeResponseDto(Long id, Long userId, Long trackId) {
		this.id = id;
		this.userId = userId;
		this.trackId = trackId;
	}

	public static LikeResponseDto toDto(Like like) {
		return LikeResponseDto.builder()
			.id(like.getId())
			.userId(like.getUser().getId())
			.trackId(like.getTrack().getId())
			.build();
	}
}
