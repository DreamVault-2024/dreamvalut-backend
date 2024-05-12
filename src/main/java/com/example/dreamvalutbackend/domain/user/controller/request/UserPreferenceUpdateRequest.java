package com.example.dreamvalutbackend.domain.user.controller.request;

import java.util.List;

import com.example.dreamvalutbackend.domain.user.controller.response.UserGenreResponseDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPreferenceUpdateRequest {
	private String displayName;
	private List<UserGenreResponseDto> genres;

	@Builder
	UserPreferenceUpdateRequest(String displayName, List<UserGenreResponseDto> genres) {
		this.displayName = displayName;
		this.genres = genres;
	}



}
