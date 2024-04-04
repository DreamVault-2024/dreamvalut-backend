package com.example.dreamvalutbackend.domain.track.controller.response;

import com.example.dreamvalutbackend.domain.track.domain.Track;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TrackUploadResponseDto {

    private Long trackId;
    private String title;
    private Boolean hasLyrics;
    private String trackUrl;
    private String trackImage;
    private String thumbnailImage;

    public TrackUploadResponseDto(Track track) {
        this.trackId = track.getId();
        this.title = track.getTitle();
        this.hasLyrics = track.getHasLyrics();
        this.trackUrl = track.getTrackUrl();
        this.trackImage = track.getTrackImage();
        this.thumbnailImage = track.getThumbnailImage();
    }

    // For Testing
    @Builder
    public TrackUploadResponseDto(Long trackId, String title, Boolean hasLyrics, String trackUrl, String trackImage,
            String thumbnailImage) {
        this.trackId = trackId;
        this.title = title;
        this.hasLyrics = hasLyrics;
        this.trackUrl = trackUrl;
        this.trackImage = trackImage;
        this.thumbnailImage = thumbnailImage;
    }
}
