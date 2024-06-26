package com.example.dreamvalutbackend.domain.track.controller.response;

import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TrackResponseDto {

    private Long trackId;
    private String title;
    private String uploaderName;
    private Integer duration;
    private Boolean hasLyrics;
    private String trackUrl;
    private String trackImage;
    private String thumbnailImage;
    private String prompt;
    private Long likes;
    private Boolean likesFlag;

    @Builder
    public TrackResponseDto(Long trackId, String title, String uploaderName, Integer duration, Boolean hasLyrics,
            String trackUrl, String trackImage, String thumbnailImage, String prompt, Long likes, Boolean likesFlag) {
        this.trackId = trackId;
        this.title = title;
        this.uploaderName = uploaderName;
        this.duration = duration;
        this.hasLyrics = hasLyrics;
        this.trackUrl = trackUrl;
        this.trackImage = trackImage;
        this.thumbnailImage = thumbnailImage;
        this.prompt = prompt;
        this.likes = likes;
        this.likesFlag = likesFlag;
    }

    public static TrackResponseDto toDto(Track track, TrackDetail trackDetail, Long likes, Boolean likesFlag) {
        return TrackResponseDto.builder()
                .trackId(track.getId())
                .title(track.getTitle())
                .uploaderName(track.getUser().getDisplayName())
                .duration(track.getDuration())
                .hasLyrics(track.getHasLyrics())
                .trackUrl(track.getTrackUrl())
                .trackImage(track.getTrackImage())
                .thumbnailImage(track.getThumbnailImage())
                .prompt(trackDetail.getPrompt())
                .likes(likes)
                .likesFlag(likesFlag)
                .build();
    }
}
