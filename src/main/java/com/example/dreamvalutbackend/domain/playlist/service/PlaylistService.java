package com.example.dreamvalutbackend.domain.playlist.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.CreatePlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.domain.MyPlaylist;
import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.playlist.repository.MyPlaylistRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final MyPlaylistRepository myPlaylistRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreatePlaylistResponseDto createPlaylist(CreatePlaylistRequestDto createPlaylistRequestDto) {
        // 유저 가져오기
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + 1L));

        // Playlist 생성
        Playlist playlist = Playlist.builder()
                .playlistName(createPlaylistRequestDto.getPlaylistName())
                .isPublic(createPlaylistRequestDto.getIsPublic())
                .isCurated(false)
                .user(user)
                .build();
        Playlist savedPlaylist = playlistRepository.save(playlist);

        // MyPlaylist 생성
        MyPlaylist myPlaylist = MyPlaylist.builder()
                .playlist(savedPlaylist)
                .user(user)
                .build();
        myPlaylistRepository.save(myPlaylist);

        return CreatePlaylistResponseDto.toDto(savedPlaylist);
    }
}
