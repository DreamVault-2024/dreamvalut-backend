package com.example.dreamvalutbackend.domain.genre.controller.response;

import com.example.dreamvalutbackend.domain.track.domain.Track;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TrackOverviewResponseDto {

    private Long trackId;
    private String title;
    private String uploaderName;
    private String thumbnailImage;

    @Builder
    public TrackOverviewResponseDto(Long trackId, String title, String uploaderName, String thumbnailImage) {
        this.trackId = trackId;
        this.title = title;
        this.uploaderName = uploaderName;
        this.thumbnailImage = thumbnailImage;
    }

    public static TrackOverviewResponseDto toDto(Track track) {
        return TrackOverviewResponseDto.builder()
                .trackId(track.getId())
                .title(track.getTitle())
                .uploaderName(track.getUser().getDisplayName())
                .thumbnailImage(track.getThumbnailImage())
                .build();
    }
}
