package com.example.dreamvalutbackend.domain.user.controller.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGenreCreateRequestDto {
	@NotEmpty
	private List<Long> genreIds;

	@Builder
	UserGenreCreateRequestDto(List<Long> genreIds) {
		this.genreIds = genreIds;
	}
}
