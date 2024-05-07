package com.example.dreamvalutbackend.domain.like.controller.response;

import org.springframework.data.domain.Page;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeWithTracksOverviewResponseDto {
	private Page<TrackResponseDto> tracks;

	@Builder
	public LikeWithTracksOverviewResponseDto(Page<TrackResponseDto> tracks) {
		this.tracks = tracks;
	}

	public static LikeWithTracksOverviewResponseDto toDto(Page<TrackResponseDto> tracks) {
		return LikeWithTracksOverviewResponseDto.builder()
			.tracks(tracks)
			.build();
	}
}
