package com.example.dreamvalutbackend.domain.track.controller.response;

import java.util.List;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserCreateTrackResponseDto {

	private Long playlistId;
	private String playlistName;
	private List<String> thumbnails;

	@Builder
	UserCreateTrackResponseDto(Long playlistId, String playlistName, List<String> thumbnails) {
		this.playlistId = playlistId;
		this.playlistName = playlistName;
		this.thumbnails = thumbnails;
	}

	public static UserCreateTrackResponseDto toDto(Playlist playlist,List<String> thumbnails) {
		return UserCreateTrackResponseDto.builder()
			.playlistId(playlist.getId())
			.playlistName(playlist.getPlaylistName())
			.thumbnails(thumbnails)
			.build();
	}


}
