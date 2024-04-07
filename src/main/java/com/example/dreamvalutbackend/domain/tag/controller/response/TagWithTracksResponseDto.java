package com.example.dreamvalutbackend.domain.tag.controller.response;

import org.springframework.data.domain.Page;

import com.example.dreamvalutbackend.domain.tag.domain.Tag;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TagWithTracksResponseDto {

    private Long tagId;
    private String tagName;
    private String tagImage;
    private Page<TrackResponseDto> tracks;

    @Builder
    public TagWithTracksResponseDto(Long tagId, String tagName, String tagImage, Page<TrackResponseDto> tracks) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.tagImage = tagImage;
        this.tracks = tracks;
    }

    public static TagWithTracksResponseDto toDto(Tag tag, Page<TrackResponseDto> tracks) {
        return TagWithTracksResponseDto.builder()
                .tagId(tag.getId())
                .tagName(tag.getTagName())
                .tagImage(tag.getTagImage())
                .tracks(tracks)
                .build();
    }
}
