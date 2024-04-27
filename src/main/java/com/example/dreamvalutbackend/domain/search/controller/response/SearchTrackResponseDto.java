package com.example.dreamvalutbackend.domain.search.controller.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchTrackResponseDto {

    private Long id;
    private String title;
    private String uploaderName;
    private String prompt;
    private SearchTrackGenreResponseDto trackGenre;
    private List<SearchTrackTagResponseDto> trackTags;
    private Long likes;
    private Boolean likesFlag;
    private String thumbnailImage;

    @Builder
    public SearchTrackResponseDto(Long id, String title, String uploaderName, String prompt,
            SearchTrackGenreResponseDto trackGenre, List<SearchTrackTagResponseDto> trackTags, Long likes,
            Boolean likesFlag, String thumbnailImage) {
        this.id = id;
        this.title = title;
        this.uploaderName = uploaderName;
        this.prompt = prompt;
        this.trackGenre = trackGenre;
        this.trackTags = trackTags;
        this.likes = likes;
        this.likesFlag = likesFlag;
        this.thumbnailImage = thumbnailImage;
    }
}
