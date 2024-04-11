package com.example.dreamvalutbackend.domain.playlist.controller.response;

import java.util.List;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackOverviewResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaylistWithTracksOverviewResponseDto {

    private Long playlistId;
    private String playlistName;
    private Boolean isPublic;
    private Boolean isCurated;
    private String ownerName;
    private List<TrackOverviewResponseDto> tracks;

    @Builder
    public PlaylistWithTracksOverviewResponseDto(Long playlistId, String playlistName, Boolean isPublic, Boolean isCurated,
            String ownerName, List<TrackOverviewResponseDto> tracks) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.isPublic = isPublic;
        this.isCurated = isCurated;
        this.ownerName = ownerName;
        this.tracks = tracks;
    }

    public static PlaylistWithTracksOverviewResponseDto toDto(Playlist playlist, List<TrackOverviewResponseDto> tracks) {
        return PlaylistWithTracksOverviewResponseDto.builder()
                .playlistId(playlist.getId())
                .playlistName(playlist.getPlaylistName())
                .isPublic(playlist.getIsPublic())
                .isCurated(playlist.getIsCurated())
                .ownerName(playlist.getUser() == null ? null : playlist.getUser().getDisplayName())
                .tracks(tracks)
                .build();
    }
}
