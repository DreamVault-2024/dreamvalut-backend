package com.example.dreamvalutbackend.domain.track.controller.response;

import com.example.dreamvalutbackend.domain.track.domain.Track;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TrackUploadResponseDto {

    private Long trackId;
    private String title;
    private Integer duration;
    private Boolean hasLyrics;
    private String trackUrl;
    private String trackImage;
    private String thumbnailImage;

    @Builder
    public TrackUploadResponseDto(Long trackId, String title, Integer duration, Boolean hasLyrics, String trackUrl, String trackImage,
            String thumbnailImage) {
        this.trackId = trackId;
        this.title = title;
        this.duration = duration;
        this.hasLyrics = hasLyrics;
        this.trackUrl = trackUrl;
        this.trackImage = trackImage;
        this.thumbnailImage = thumbnailImage;
    }

    public static TrackUploadResponseDto toDto(Track track) {
        return TrackUploadResponseDto.builder()
                .trackId(track.getId())
                .title(track.getTitle())
                .duration(track.getDuration())
                .hasLyrics(track.getHasLyrics())
                .trackUrl(track.getTrackUrl())
                .trackImage(track.getTrackImage())
                .thumbnailImage(track.getThumbnailImage())
                .build();
    }
}
