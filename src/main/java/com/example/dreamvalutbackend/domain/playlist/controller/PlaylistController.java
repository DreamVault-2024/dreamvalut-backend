package com.example.dreamvalutbackend.domain.playlist.controller;

import java.net.URI;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.UpdatePlaylistNameRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.playlist.service.PlaylistService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<PlaylistResponseDto> createPlaylist(
            @RequestBody CreatePlaylistRequestDto createPlaylistRequestDto) {
        PlaylistResponseDto createPlaylistResponseDto = playlistService.createPlaylist(createPlaylistRequestDto);

        // HTTP 201 Created 상태 코드와 함께 생성된 리소스의 URI를 반환
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{playlist_id}")
                .buildAndExpand(createPlaylistResponseDto.getPlaylistId())
                .toUri();

        return ResponseEntity.created(location)
                .body(createPlaylistResponseDto);
    }

    @GetMapping("/{playlist_id}")
    public ResponseEntity<PlaylistWithTracksResponseDto> getPlaylistWithTracks(
            @PathVariable("playlist_id") Long playlistId,
            @PageableDefault(page = 0, size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(playlistService.getPlaylistWithTracks(playlistId, pageable));
    }

    @PatchMapping("/{playlist_id}")
    public ResponseEntity<PlaylistResponseDto> updatePlaylistName(
            @PathVariable("playlist_id") Long playlistId,
            @RequestBody UpdatePlaylistNameRequestDto updatePlaylistNameRequestDto) {
        return ResponseEntity.ok(playlistService.updatePlaylistName(playlistId, updatePlaylistNameRequestDto));
    }

    @DeleteMapping("/{playlist_id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable("playlist_id") Long playlistId) {
        playlistService.deletePlaylist(playlistId);
        return ResponseEntity.noContent().build();
    }
}
