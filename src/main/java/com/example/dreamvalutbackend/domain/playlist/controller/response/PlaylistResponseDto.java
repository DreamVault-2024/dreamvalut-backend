package com.example.dreamvalutbackend.domain.playlist.controller.response;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaylistResponseDto {

    private Long playlistId;
    private String playlistName;
    private Boolean isPublic;
    private Boolean isCurated;

    @Builder
    public PlaylistResponseDto(Long playlistId, String playlistName, Boolean isPublic, Boolean isCurated) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.isPublic = isPublic;
        this.isCurated = isCurated;
    }

    public static PlaylistResponseDto toDto(Playlist playlist) {
        return PlaylistResponseDto.builder()
                .playlistId(playlist.getId())
                .playlistName(playlist.getPlaylistName())
                .isPublic(playlist.getIsPublic())
                .isCurated(playlist.getIsCurated())
                .build();
    }
}
