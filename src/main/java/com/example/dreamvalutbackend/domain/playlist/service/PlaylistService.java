package com.example.dreamvalutbackend.domain.playlist.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.UpdatePlaylistNameRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.playlist.domain.MyPlaylist;
import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.playlist.domain.PlaylistTrack;
import com.example.dreamvalutbackend.domain.playlist.domain.PlaylistTrack;
import com.example.dreamvalutbackend.domain.playlist.repository.MyPlaylistRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistTrackRepository;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistTrackRepository;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final MyPlaylistRepository myPlaylistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackDetailRepository trackDetailRepository;
    private final UserRepository userRepository;

    @Transactional
    public PlaylistResponseDto createPlaylist(CreatePlaylistRequestDto createPlaylistRequestDto) {
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

        return PlaylistResponseDto.toDto(savedPlaylist);
    }

    public PlaylistWithTracksResponseDto getPlaylistWithTracks(Long playlistId, Pageable pageable) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + playlistId));

        Page<TrackResponseDto> tracks = playlistTrackRepository.findAllByPlaylistId(playlistId, pageable)
                .map(playlistTrack -> {
                    // Track 가져오기
                    Track track = playlistTrack.getTrack();

                    // TrackDetail 가져오기
                    TrackDetail trackDetail = trackDetailRepository.findById(track.getId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "TrackDetail not found for track id: " + track.getId()));

                    // TrackResponseDto 생성
                    return TrackResponseDto.toDto(track, trackDetail);
                });

        return PlaylistWithTracksResponseDto.toDto(playlist, tracks);
    }

    @Transactional
    public PlaylistResponseDto updatePlaylistName(Long playlistId,
            UpdatePlaylistNameRequestDto updatePlaylistNameRequestDto) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + playlistId));

        // TODO: 로그인한 유저와 Playlist의 유저가 같은지 확인
        // 어떤 필드로 검증 할 것인지는 JWT 토큰에 담긴 정보에 따라서 결정
        // if (!playlist.getUser().getUserName().equals("temp")) {
        // throw new SecurityException("User not authorized to update this playlist");
        // }

        playlist.updatePlaylistName(updatePlaylistNameRequestDto.getPlaylistName());

        return PlaylistResponseDto.toDto(playlist);
    }
}
