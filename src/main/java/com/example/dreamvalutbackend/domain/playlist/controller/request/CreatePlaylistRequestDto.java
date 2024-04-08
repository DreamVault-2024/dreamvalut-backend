package com.example.dreamvalutbackend.domain.playlist.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePlaylistRequestDto {

    @NotNull
    private String playlistName;

    @NotNull
    private Boolean isPublic;

    @Builder
    public CreatePlaylistRequestDto(String playlistName, Boolean isPublic) {
        this.playlistName = playlistName;
        this.isPublic = isPublic;
    }
}
