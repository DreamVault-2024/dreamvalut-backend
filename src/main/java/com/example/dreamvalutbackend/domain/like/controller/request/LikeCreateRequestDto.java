package com.example.dreamvalutbackend.domain.like.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeCreateRequestDto {
	@NotNull
	private Long userId;
	@NotNull
	private Long trackId;

	@Builder
	public LikeCreateRequestDto(Long userId, Long trackId) {
		this.userId = userId;
		this.trackId = trackId;
	}

}
