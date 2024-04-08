package com.example.dreamvalutbackend.domain.tag.controller.response;

import com.example.dreamvalutbackend.domain.tag.domain.Tag;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TagResponseDto {

    private Long tagId;
    private String tagName;
    private String tagImage;

    @Builder
    public TagResponseDto(Long tagId, String tagName, String tagImage) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.tagImage = tagImage;
    }

    public static TagResponseDto toDto(Tag tag) {
        return TagResponseDto.builder()
                .tagId(tag.getId())
                .tagName(tag.getTagName())
                .tagImage(tag.getTagImage())
                .build();
    }
}
