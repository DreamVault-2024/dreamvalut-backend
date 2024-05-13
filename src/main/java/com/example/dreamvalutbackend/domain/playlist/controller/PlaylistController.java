package com.example.dreamvalutbackend.domain.playlist.controller;

import java.net.URI;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.dreamvalutbackend.domain.playlist.controller.request.AddTrackToPlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.UpdatePlaylistNameRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.playlist.service.PlaylistService;
import com.example.dreamvalutbackend.domain.playlist.validation.annotation.ValidPlaylistType;
import com.example.dreamvalutbackend.domain.track.controller.response.UserCreateTrackResponseDto;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    @Operation(summary = "새 플레이리스트 생성")
    public ResponseEntity<PlaylistResponseDto> createPlaylist(
            @RequestBody CreatePlaylistRequestDto createPlaylistRequestDto,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

        PlaylistResponseDto createPlaylistResponseDto = playlistService.createPlaylist(createPlaylistRequestDto,
                userDetailPrincipal.getUserId());

        // HTTP 201 Created 상태 코드와 함께 생성된 리소스의 URI를 반환
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{playlist_id}")
                .buildAndExpand(createPlaylistResponseDto.getPlaylistId())
                .toUri();

        return ResponseEntity.created(location)
                .body(createPlaylistResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<PlaylistWithTracksOverviewResponseDto>> getPlaylistsWithTracksOverview(
            @ValidPlaylistType @RequestParam("type") String type,
            @PageableDefault(page = 0, size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(playlistService.getPlaylistsWithTracksOverview(type, pageable));
    }

    @GetMapping("/{playlist_id}")
    @Operation(summary = "특정 플레이스트 정보 가져오기")
    public ResponseEntity<PlaylistWithTracksResponseDto> getPlaylistWithTracks(
            @PathVariable("playlist_id") Long playlistId,
            @PageableDefault(page = 0, size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

        return ResponseEntity
                .ok(playlistService.getPlaylistWithTracks(playlistId, pageable, userDetailPrincipal.getUserId()));
    }

    @PatchMapping("/{playlist_id}")
    @Operation(summary = "특정 플레이리스트 이름 수정하기")
    public ResponseEntity<PlaylistResponseDto> updatePlaylistName(
            @PathVariable("playlist_id") Long playlistId,
            @RequestBody UpdatePlaylistNameRequestDto updatePlaylistNameRequestDto,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

        return ResponseEntity.ok(playlistService.updatePlaylistName(playlistId, updatePlaylistNameRequestDto,
                userDetailPrincipal.getUserId()));
    }

    @DeleteMapping("/{playlist_id}")
    @Operation(summary = "특정 플레이리스트 삭제하기")
    public ResponseEntity<Void> deletePlaylist(@PathVariable("playlist_id") Long playlistId,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

        playlistService.deletePlaylist(playlistId, userDetailPrincipal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playlist_id}/tracks")
    @Operation(summary = "특정 플레이리스트에 곡 추가하기")
    public ResponseEntity<Void> addTrackToPlaylist(@PathVariable("playlist_id") Long playlistId,
            @RequestBody AddTrackToPlaylistRequestDto addTrackToPlaylistRequestDto,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

        playlistService.addTrackToPlaylist(playlistId, addTrackToPlaylistRequestDto, userDetailPrincipal.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{playlist_id}/tracks/{track_id}")
    @Operation(summary = "특정 플레이리스트의 곡 삭제하기")
    public ResponseEntity<Void> deleteTrackFromPlaylist(@PathVariable("playlist_id") Long playlistId,
            @PathVariable("track_id") Long trackId,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

        playlistService.deleteTrackFromPlaylist(playlistId, trackId, userDetailPrincipal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playlist_id}/follow")
    public ResponseEntity<Void> followPlaylist(@PathVariable("playlist_id") Long playlistId,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

        playlistService.followPlaylist(playlistId, userDetailPrincipal.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{playlist_id}/follow")
    public ResponseEntity<Void> unfollowPlaylist(@PathVariable("playlist_id") Long playlistId,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

        playlistService.unfollowPlaylist(playlistId, userDetailPrincipal.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/created")
    public ResponseEntity<Page<UserCreateTrackResponseDto>> getUserCreatedPlaylists(
        @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal,
        @PageableDefault(page = 0, size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Long userId = userDetailPrincipal.getUserId();
        Page<UserCreateTrackResponseDto> userPlaylists = playlistService.findUserCreateTrack(userId, pageable);

        return ResponseEntity.ok(userPlaylists);
    }

    @GetMapping("/users/followed")
    public ResponseEntity<Page<UserCreateTrackResponseDto>> getFollowedUserPlaylists(
        @RequestParam String type,
        @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal,
        @PageableDefault(page = 0, size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Long userId = userDetailPrincipal.getUserId();
        Page<UserCreateTrackResponseDto> followedPlaylists = playlistService.findFollowedUserTrack(userId, type, pageable);

        return ResponseEntity.ok(followedPlaylists);
    }

    @GetMapping("/users/list")
    public ResponseEntity<Page<PlaylistResponseDto>> getUserPlaylists(
        @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal,
        @PageableDefault(page = 0, size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Long userId = userDetailPrincipal.getUserId();
        Page<PlaylistResponseDto> playlists = playlistService.findUserPlaylist(userId, pageable);

        return ResponseEntity.ok(playlists);
    }
}
