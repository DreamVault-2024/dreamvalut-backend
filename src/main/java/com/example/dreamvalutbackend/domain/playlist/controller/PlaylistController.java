package com.example.dreamvalutbackend.domain.playlist.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.CreatePlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.service.PlaylistService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<CreatePlaylistResponseDto> createPlaylist(
            @RequestBody CreatePlaylistRequestDto createPlaylistRequestDto) {
        CreatePlaylistResponseDto createPlaylistResponseDto = playlistService.createPlaylist(createPlaylistRequestDto);

        // HTTP 201 Created 상태 코드와 함께 생성된 리소스의 URI를 반환
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{playlist_id}")
                .buildAndExpand(createPlaylistResponseDto.getPlaylistId())
                .toUri();

        return ResponseEntity.created(location)
                .body(createPlaylistResponseDto);
    }
}
