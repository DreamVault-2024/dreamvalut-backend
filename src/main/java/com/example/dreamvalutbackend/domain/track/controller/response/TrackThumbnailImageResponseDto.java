package com.example.dreamvalutbackend.domain.track.controller.response;

import java.util.List;

import com.example.dreamvalutbackend.domain.track.domain.Track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TrackThumbnailImageResponseDto {
	private String title;
	private List<String> thumbnails;

	@Builder
	public TrackThumbnailImageResponseDto(String title,List<String> thumbnails) {
		this.title = title;
		this.thumbnails = thumbnails;
	}

	public static TrackThumbnailImageResponseDto toDto(List<String> thumbnails) {
		return TrackThumbnailImageResponseDto.builder()
			.title("좋아요 누른 곡")
			.thumbnails(thumbnails)
			.build();
	}
}
