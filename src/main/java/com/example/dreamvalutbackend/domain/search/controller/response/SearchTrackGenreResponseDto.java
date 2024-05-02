package com.example.dreamvalutbackend.domain.search.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchTrackGenreResponseDto {

    private Long genreId;
    private String genreName;

    @Builder
    public SearchTrackGenreResponseDto(Long genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }
}
