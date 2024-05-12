package com.example.dreamvalutbackend.domain.track.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackUploadRequestDto {

    @NotNull
    private String title;

    @NotNull
    private String prompt;

    @NotNull
    private Boolean hasLyrics;

    @NotNull
    private String[] tags;

    @NotNull
    private Long genreId;

    // For Testing
    @Builder
    public TrackUploadRequestDto(String title, String prompt, Boolean hasLyrics, String[] tags, Long genreId) {
        this.title = title;
        this.prompt = prompt;
        this.hasLyrics = hasLyrics;
        this.tags = tags;
        this.genreId = genreId;
    }
}
