package com.example.dreamvalutbackend.domain.playlist.controller.response;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatePlaylistResponseDto {

    private long playlistId;
    private String playlistName;
    private Boolean isPublic;
    private Boolean isCurated;

    @Builder
    public CreatePlaylistResponseDto(long playlistId, String playlistName, Boolean isPublic, Boolean isCurated) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.isPublic = isPublic;
        this.isCurated = isCurated;
    }

    public static CreatePlaylistResponseDto toDto(Playlist playlist) {
        return CreatePlaylistResponseDto.builder()
                .playlistId(playlist.getId())
                .playlistName(playlist.getPlaylistName())
                .isPublic(playlist.getIsPublic())
                .isCurated(playlist.getIsCurated())
                .build();
    }
}
