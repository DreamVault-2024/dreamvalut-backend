package com.example.dreamvalutbackend.domain.user.controller.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPreferenceUpdateRequest {
	private String displayName;
	@NotEmpty
	private List<Long> genreIds;

	@Builder
	UserPreferenceUpdateRequest(String displayName, List<Long> genreIds) {
		this.displayName = displayName;
		this.genreIds = genreIds;
	}



}
