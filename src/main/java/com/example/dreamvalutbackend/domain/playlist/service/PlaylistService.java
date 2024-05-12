package com.example.dreamvalutbackend.domain.playlist.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.like.repository.LikeRepository;
import com.example.dreamvalutbackend.domain.playlist.controller.request.AddTrackToPlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.UpdatePlaylistNameRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.playlist.domain.MyPlaylist;
import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.playlist.domain.PlaylistTrack;
import com.example.dreamvalutbackend.domain.playlist.repository.MyPlaylistRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistTrackRepository;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackOverviewResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.UserCreateTrackResponseDto;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;
import java.util.stream.Stream;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final MyPlaylistRepository myPlaylistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackRepository trackRepository;
    private final TrackDetailRepository trackDetailRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public PlaylistResponseDto createPlaylist(CreatePlaylistRequestDto createPlaylistRequestDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Playlist 생성
        Playlist playlist = Playlist.builder()
            .playlistName(createPlaylistRequestDto.getPlaylistName())
            .isPublic(createPlaylistRequestDto.getIsPublic())
            .isCurated(false)
            .user(user)
            .build();
        Playlist savedPlaylist = playlistRepository.save(playlist);

        return PlaylistResponseDto.toDto(savedPlaylist);
    }

    @Transactional(readOnly = true)
    public Page<PlaylistWithTracksOverviewResponseDto> getPlaylistsWithTracksOverview(String type, Pageable pageable) {

        // type에 따라서 가져올 Query Function 명시
        Map<String, Function<Pageable, Page<Playlist>>> typeToQueryFunction = Map.of(
            "curated", playlistRepository::findByIsCuratedTrue,
            "user_created", playlistRepository::findByIsCuratedFalseAndIsPublicTrue);

        // Playlist 가져오기
        Page<Playlist> playlistPage = typeToQueryFunction.get(type).apply(pageable);

        // PlaylistWithTracksOverviewResponseDto로 변환하여 반환
        return playlistPage.map(playlist -> {
            // Playlist에 해당하는 Track 가져오기
            List<TrackOverviewResponseDto> tracks = playlistTrackRepository
                .findAllByPlaylistId(playlist.getId(), PageRequest.of(0, 3, Sort.by("createdAt").descending()))
                .stream()
                .map(PlaylistTrack::getTrack)
                .map(TrackOverviewResponseDto::toDto)
                .collect(Collectors.toList());
            return PlaylistWithTracksOverviewResponseDto.toDto(playlist, tracks);
        });
    }

    @Transactional(readOnly = true)
    public PlaylistWithTracksResponseDto getPlaylistWithTracks(Long playlistId, Pageable pageable, Long userId) {

        // ID로 Playlist 찾기
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + playlistId));
      
        // Playlist이 비공개이고 로그인한 유저와 Playlist의 유저가 다르면 예외 발생
        Boolean isOwner = playlist.getUser().getUserId().equals(userId);
        if (!playlist.getIsPublic() && !isOwner) {
            throw new SecurityException("User not authorized to view this playlist");
        }

        // Playlist에 해당하는 Track들 가져오기
        Page<TrackResponseDto> tracks = playlistTrackRepository.findAllByPlaylistId(playlistId, pageable)
            .map(playlistTrack -> {
                // Track 가져오기
                Track track = playlistTrack.getTrack();

                // TrackDetail 가져오기
                TrackDetail trackDetail = trackDetailRepository.findById(track.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                        "TrackDetail not found for track id: " + track.getId()));

                Long likes = likeRepository.countByTrackId(track.getId());
                Boolean likesFlag = likeRepository.existsByUserIdAndTrackId(userId, track.getId());

                // TrackResponseDto 생성
                return TrackResponseDto.toDto(track, trackDetail, likes, likesFlag);
            });


        return PlaylistWithTracksResponseDto.toDto(playlist, tracks, isOwner);
    }

    @Transactional
    public PlaylistResponseDto updatePlaylistName(Long playlistId,

            UpdatePlaylistNameRequestDto updatePlaylistNameRequestDto, Long userId) {

        // ID로 Playlist 찾기
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + playlistId));

        // Playlist의 유저와 로그인한 유저가 다르면 예외 발생
        User playlistOwner = playlist.getUser();
        if (!playlistOwner.getUserId().equals(userId)) {
            throw new SecurityException("User not authorized to update this playlist");
        }

        // Playlist 이름 업데이트
        playlist.updatePlaylistName(updatePlaylistNameRequestDto.getPlaylistName());

        return PlaylistResponseDto.toDto(playlist);
    }

    @Transactional
    public void deletePlaylist(Long playlistId, Long userId) {

        // ID로 Playlist 찾기
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + playlistId));

        // Playlist의 유저와 로그인한 유저가 다르면 예외 발생
        User playlistOwner = playlist.getUser();
        if (!playlistOwner.getUserId().equals(userId)) {
            throw new SecurityException("User not authorized to delete this playlist");
        }

        // 찾은 Playlist와 연관된 PlaylistTrack 삭제
        playlistTrackRepository.deleteByPlaylist(playlist);

        // 찾은 Playlist 삭제
        playlistRepository.delete(playlist);
    }

    @Transactional
    public void addTrackToPlaylist(Long playlistId,
            AddTrackToPlaylistRequestDto addTrackToPlaylistRequestDto, Long userId) {

        // ID로 Playlist 찾기
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + playlistId));

        // Playlist의 유저와 로그인한 유저가 다르면 예외 발생
        User playlistOwner = playlist.getUser();
        if (!playlistOwner.getUserId().equals(userId)) {
            throw new SecurityException("User not authorized to delete this playlist");
        }

        // ID로 Track 찾기
        Track track = trackRepository.findById(addTrackToPlaylistRequestDto.getTrackId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Track not found with id: " + addTrackToPlaylistRequestDto.getTrackId()));

        // 이미 Playlist에 추가된 Track인지 확인
        if (playlistTrackRepository.existsByPlaylistAndTrack(playlist, track)) {
            throw new IllegalArgumentException("Track is already in the playlist");
        }

        // PlaylistTrack 생성
        PlaylistTrack playlistTrack = PlaylistTrack.builder()
            .playlist(playlist)
            .track(track)
            .build();
        playlistTrackRepository.save(playlistTrack);
    }

    @Transactional
    public void deleteTrackFromPlaylist(Long playlistId, Long trackId, Long userId) {

        // ID로 Playlist 찾기
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + playlistId));

        // Playlist의 유저와 로그인한 유저가 다르면 예외 발생
        User playlistOwner = playlist.getUser();
        if (!playlistOwner.getUserId().equals(userId)) {
            throw new SecurityException("User not authorized to delete this playlist");
        }

        // ID로 Track 찾기
        Track track = trackRepository.findById(trackId)
            .orElseThrow(() -> new EntityNotFoundException("Track not found with id: " + trackId));

        // PlaylistTrack 찾기
        PlaylistTrack playlistTrack = playlistTrackRepository.findByPlaylistAndTrack(playlist, track)
            .orElseThrow(() -> new EntityNotFoundException("PlaylistTrack not found with playlist id: " + playlistId
                + " and track id: " + trackId));

        // PlaylistTrack 삭제
        playlistTrackRepository.delete(playlistTrack);
    }

    @Transactional
    public void followPlaylist(Long playlistId, Long userId) {

        // ID로 User 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // ID로 Playlist 찾기
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + playlistId));

        // 이미 Playlist을 팔로우 중인지 확인
        if (myPlaylistRepository.existsByUserAndPlaylist(user, playlist)) {
            throw new IllegalArgumentException("Playlist is already followed by the user");
        }

        // MyPlaylist 생성
        MyPlaylist myPlaylist = MyPlaylist.builder()
            .playlist(playlist)
            .user(user)
            .build();
        myPlaylistRepository.save(myPlaylist);
    }

    @Transactional
    public void unfollowPlaylist(Long playlistId, Long userId) {

        // ID로 User 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // ID로 Playlist 찾기
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new EntityNotFoundException("Playlist not found with id: " + playlistId));

        // MyPlaylist 찾기 (팔로우 중인지 확인)
        MyPlaylist myPlaylist = myPlaylistRepository.findByUserAndPlaylist(user, playlist)
            .orElseThrow(() -> new EntityNotFoundException("User is not following this playlist"));

        // MyPlaylist 삭제
        myPlaylistRepository.delete(myPlaylist);
    }

    @Transactional(readOnly = true)
    public Page<UserCreateTrackResponseDto> findUserCreateTrack(Long userId, Pageable pageable) {
        Page<Playlist> playlists = playlistRepository.findAllByUser_UserId(userId, pageable);

        return playlists.map(playlist -> {
            List<String> thumbnails = playlistTrackRepository.findByPlaylist(playlist).stream()
                .map(PlaylistTrack::getTrack)
                .limit(3)
                .map(Track::getThumbnailImage)
                .collect(Collectors.toList());
            return UserCreateTrackResponseDto.toDto(playlist, thumbnails);
        });
    }

    @Transactional(readOnly = true)
    public Page<UserCreateTrackResponseDto> findFollowedUserTrack(Long userId, Pageable pageable) {
        Page<MyPlaylist> myPlaylists = myPlaylistRepository.findAllByUser_UserId(userId, pageable);

        return myPlaylists.map(myPlaylist -> {
            List<String> thumbnails = playlistTrackRepository.findByPlaylist(myPlaylist.getPlaylist()).stream()
                .map(PlaylistTrack::getTrack)
                .limit(3)
                .map(Track::getThumbnailImage)
                .collect(Collectors.toList());
            return UserCreateTrackResponseDto.toDto(myPlaylist.getPlaylist(), thumbnails);
        });
    }

    @Transactional(readOnly = true)
    public Page<PlaylistResponseDto> findUserPlaylist(Long userId, Pageable pageable) {
        Page<Playlist> createdPlaylists = playlistRepository.findAllByUser_UserId(userId, pageable);
        return createdPlaylists.map(playlist -> PlaylistResponseDto.toDto(playlist));
    }
}
