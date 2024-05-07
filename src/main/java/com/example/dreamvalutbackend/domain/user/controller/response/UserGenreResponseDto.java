package com.example.dreamvalutbackend.domain.user.controller.response;

import java.util.List;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.user.domain.User;


import lombok.Builder;
import lombok.Getter;

@Getter
public class UserGenreResponseDto {
	private Long genreId;
	private String genreName;
	private Boolean state;

	@Builder
	public UserGenreResponseDto(Long genreId, String genreName, Boolean state) {
		this.genreId = genreId;
		this.genreName = genreName;
		this.state = state;
	}

	public static UserGenreResponseDto toDto(Genre genre, boolean isSelected) {
		return UserGenreResponseDto.builder()
			.genreId(genre.getId())
			.genreName(genre.getGenreName())
			.state(isSelected)
			.build();
	}

}
