package com.example.dreamvalutbackend.domain.playlist.controller.response;

import org.springframework.data.domain.Page;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaylistWithTracksResponseDto {

    private Long playlistId;
    private String playlistName;
    private Boolean isPublic;
    private Boolean isCurated;
    private String ownerName;
    private Page<TrackResponseDto> tracks;

    @Builder
    public PlaylistWithTracksResponseDto(Long playlistId, String playlistName, Boolean isPublic, Boolean isCurated,
            String ownerName, Page<TrackResponseDto> tracks) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.isPublic = isPublic;
        this.isCurated = isCurated;
        this.ownerName = ownerName;
        this.tracks = tracks;
    }

    public static PlaylistWithTracksResponseDto toDto(Playlist playlist, Page<TrackResponseDto> tracks) {
        return PlaylistWithTracksResponseDto.builder()
                .playlistId(playlist.getId())
                .playlistName(playlist.getPlaylistName())
                .isPublic(playlist.getIsPublic())
                .isCurated(playlist.getIsCurated())
                .ownerName(playlist.getUser().getDisplayName())
                .tracks(tracks)
                .build();
    }
}
