package com.example.dreamvalutbackend.domain.track.controller.response;

import com.example.dreamvalutbackend.domain.track.domain.Track;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TrackRankResponseDto {
	private Long trackId;
	private Integer rank;
	private String title;
	private String uploaderName;
	private String thumbnailImage;

	@Builder
	public TrackRankResponseDto(Long trackId, Integer rank, String title, String uploaderName, String thumbnailImage) {
		this.trackId = trackId;
		this.rank = rank;
		this.title = title;
		this.uploaderName = uploaderName;
		this.thumbnailImage = thumbnailImage;
	}

	public static TrackRankResponseDto toDto(Track track, int rank) {
		return TrackRankResponseDto.builder()
			.trackId(track.getId())
			.rank(rank)
			.title(track.getTitle())
			.uploaderName(track.getUser().getDisplayName())
			.thumbnailImage(track.getThumbnailImage())
			.build();
	}
}
