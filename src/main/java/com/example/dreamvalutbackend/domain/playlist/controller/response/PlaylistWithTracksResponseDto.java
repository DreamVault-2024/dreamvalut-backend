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
    private String ownerName;
    private Boolean isPublic;
    private Boolean isCurated;
    private Boolean isFollow;
    private Boolean isOwner;
    private Page<TrackResponseDto> tracks;

    @Builder
    public PlaylistWithTracksResponseDto(Long playlistId, String playlistName, String ownerName, Boolean isPublic,
            Boolean isCurated, Boolean isFollow, Boolean isOwner, Page<TrackResponseDto> tracks) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.ownerName = ownerName;
        this.isPublic = isPublic;
        this.isCurated = isCurated;
        this.isFollow = isFollow;
        this.isOwner = isOwner;
        this.tracks = tracks;
    }

    public static PlaylistWithTracksResponseDto toDto(Playlist playlist, Page<TrackResponseDto> tracks, Boolean isOwner, Boolean isFollow) {
        return PlaylistWithTracksResponseDto.builder()
                .playlistId(playlist.getId())
                .playlistName(playlist.getPlaylistName())
                .ownerName(playlist.getUser().getDisplayName())
                .isPublic(playlist.getIsPublic())
                .isCurated(playlist.getIsCurated())
                .isFollow(isFollow)
                .isOwner(isOwner)
                .tracks(tracks)
                .build();
    }
}
