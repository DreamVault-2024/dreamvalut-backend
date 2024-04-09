package com.example.dreamvalutbackend.domain.playlist.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePlaylistNameRequestDto {

    @NotNull
    private String playlistName;

    public UpdatePlaylistNameRequestDto(String playlistName) {
        this.playlistName = playlistName;
    }
}
