package com.example.dreamvalutbackend.domain.search.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchTrackTagResponseDto {

    private Long tagId;
    private String tagName;

    @Builder
    public SearchTrackTagResponseDto(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }
}
