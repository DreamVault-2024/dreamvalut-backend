package com.example.dreamvalutbackend.domain.playlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.playlist.controller.request.AddTrackToPlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.UpdatePlaylistNameRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.playlist.domain.MyPlaylist;
import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.playlist.domain.PlaylistTrack;
import com.example.dreamvalutbackend.domain.playlist.repository.MyPlaylistRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistTrackRepository;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;

public class PlaylistServiceUnitTest {

    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private MyPlaylistRepository myPlaylistRepository;
    @Mock
    private PlaylistTrackRepository playlistTrackRepository;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private TrackDetailRepository trackDetailRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlaylistService playlistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("POST /playlist - Unit Success")
    void createPlaylistSuccess() {
        /* Given */

        // 요청할 CreatePlaylistRequestDto 객체 생성
        CreatePlaylistRequestDto createPlaylistRequestDto = CreatePlaylistRequestDto.builder()
                .playlistName("Test Playlist")
                .isPublic(true)
                .build();

        // Mock User, Playlist, MyPlaylist 객체 생성
        User user = createMockUser(1L, "testUser", "Test User", "testUser@example.com", "testUserProfileImage",
                UserRole.USER, "testUserSocialId");
        Playlist playlist = createMockPlaylist(1L, "Test Playlist", true, false, user);
        MyPlaylist myPlaylist = createMockMyPlaylist(1L, playlist, user);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(playlistRepository.save(any(Playlist.class))).willReturn(playlist);
        given(myPlaylistRepository.save(any(MyPlaylist.class))).willReturn(myPlaylist);

        /* When */
        PlaylistResponseDto createPlaylistResponseDto = playlistService.createPlaylist(createPlaylistRequestDto);

        /* Then */
        assertThat(createPlaylistResponseDto.getPlaylistId()).isEqualTo(playlist.getId());
        assertThat(createPlaylistResponseDto.getPlaylistName()).isEqualTo(playlist.getPlaylistName());
        assertThat(createPlaylistResponseDto.getIsPublic()).isEqualTo(playlist.getIsPublic());
        assertThat(createPlaylistResponseDto.getIsCurated()).isEqualTo(playlist.getIsCurated());
    }

    @Test
    @DisplayName("GET /playlist/{playlist_id} - Unit Success")
    void getPlaylistWithTracksSuccess() {
        /* Given */

        // 요청할 Playlist ID
        Long playlistId = 1L;

        // Mock Playlist, Track, TrackDetail 객체 생성
        User user = createMockUser(1L, "testUser", "Test User", "testUser@example.com", "testUserProfileImage",
                UserRole.USER, "testUserSocialId");
        Genre genre = createMockGenre(1L, "Test Genre", "testGenreImage");
        Playlist playlist = createMockPlaylist(1L, "Test Playlist", true, false, user);
        Track track = createMockTrack(1L, "Test Track", 120, false, "testTrackUrl", "testTrackImage",
                "testThumbnailImage", user, genre);
        TrackDetail trackDetail = createMockTrackDetail(1L, "Test Prompt");
        PlaylistTrack playlistTrack = createMockPlaylistTrack(1L, playlist, track);
        Page<PlaylistTrack> playlistTracks = new PageImpl<>(List.of(playlistTrack));

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));
        given(playlistTrackRepository.findAllByPlaylistId(eq(playlistId), any(Pageable.class)))
                .willReturn(playlistTracks);
        given(trackDetailRepository.findById(eq(track.getId()))).willReturn(Optional.of(trackDetail));

        /* When */
        PlaylistWithTracksResponseDto playlistWithTracksResponseDto = playlistService.getPlaylistWithTracks(playlistId,
                PageRequest.of(0, 30));

        /* Then */
        assertThat(playlistWithTracksResponseDto.getPlaylistId()).isEqualTo(playlist.getId());
        assertThat(playlistWithTracksResponseDto.getPlaylistName()).isEqualTo(playlist.getPlaylistName());
        assertThat(playlistWithTracksResponseDto.getIsPublic()).isEqualTo(playlist.getIsPublic());
        assertThat(playlistWithTracksResponseDto.getIsCurated()).isEqualTo(playlist.getIsCurated());
        assertThat(playlistWithTracksResponseDto.getOwnerName()).isEqualTo(user.getDisplayName());
        assertThat(playlistWithTracksResponseDto.getTracks().getContent().get(0).getTrackId()).isEqualTo(track.getId());
        assertThat(playlistWithTracksResponseDto.getTracks().getContent().get(0).getTitle())
                .isEqualTo(track.getTitle());
        assertThat(playlistWithTracksResponseDto.getTracks().getContent().get(0).getUploaderName())
                .isEqualTo(user.getDisplayName());
        assertThat(playlistWithTracksResponseDto.getTracks().getContent().get(0).getDuration())
                .isEqualTo(track.getDuration());
        assertThat(playlistWithTracksResponseDto.getTracks().getContent().get(0).getHasLyrics())
                .isEqualTo(track.getHasLyrics());
        assertThat(playlistWithTracksResponseDto.getTracks().getContent().get(0).getTrackUrl())
                .isEqualTo(track.getTrackUrl());
        assertThat(playlistWithTracksResponseDto.getTracks().getContent().get(0).getTrackImage())
                .isEqualTo(track.getTrackImage());
        assertThat(playlistWithTracksResponseDto.getTracks().getContent().get(0).getThumbnailImage())
                .isEqualTo(track.getThumbnailImage());
        assertThat(playlistWithTracksResponseDto.getTracks().getContent().get(0).getPrompt())
                .isEqualTo(trackDetail.getPrompt());
    }

    @Test
    @DisplayName("PATCH /playlist/{playlist_id} - Unit Success")
    void updatePlaylistNameSuccess() {
        /* Given */

        // 요청할 Playlist ID
        Long playlistId = 1L;

        // 요청할 UpdatePlaylistNameRequestDto 객체 생성
        UpdatePlaylistNameRequestDto updatePlaylistNameRequestDto = new UpdatePlaylistNameRequestDto(
                "Updated Playlist Name");

        // Mock User, Playlist 객체 생성
        User user = createMockUser(1L, "testUser", "Test User", "testUser@example.com", "testUserProfileImage",
                UserRole.USER, "testUserSocialId");
        Playlist playlist = createMockPlaylist(1L, "Test Playlist", true, false, user);

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));

        /* When */
        PlaylistResponseDto updatePlaylistNameResponseDto = playlistService.updatePlaylistName(playlistId,
                updatePlaylistNameRequestDto);

        /* Then */
        assertThat(updatePlaylistNameResponseDto.getPlaylistId()).isEqualTo(playlist.getId());
        assertThat(updatePlaylistNameResponseDto.getPlaylistName())
                .isEqualTo(updatePlaylistNameRequestDto.getPlaylistName());
        assertThat(updatePlaylistNameResponseDto.getIsPublic()).isEqualTo(playlist.getIsPublic());
        assertThat(updatePlaylistNameResponseDto.getIsCurated()).isEqualTo(playlist.getIsCurated());
    }

    @Test
    @DisplayName("DELETE /playlist/{playlist_id} - Unit Success")
    void deletePlaylistSuccess() {
        /* Given */

        // 요청할 Playlist ID
        Long playlistId = 1L;

        // Mock User, Playlist 객체 생성
        User user = createMockUser(1L, "testUser", "Test User", "testUser@example.com", "testUserProfileImage",
                UserRole.USER, "testUserSocialId");
        Playlist playlist = createMockPlaylist(1L, "Test Playlist", true, false, user);

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));
        willDoNothing().given(playlistTrackRepository).deleteByPlaylist(playlist);
        willDoNothing().given(myPlaylistRepository).deleteByPlaylist(playlist);

        /* When */
        playlistService.deletePlaylist(playlistId);

        /* Then */

        // Playlist 삭제 호출 여부 확인
        verify(playlistTrackRepository).deleteByPlaylist(playlist);
        verify(myPlaylistRepository).deleteByPlaylist(playlist);
        verify(playlistRepository).delete(playlist);
    }

    @Test
    @DisplayName("POST /playlist/{playlist_id}/tracks - Unit Success")
    void addTrackToPlaylistSuccess() {
        /* Given */

        // 요청할 Playlist ID
        Long playlistId = 1L;
        Long trackId = 1L;
        AddTrackToPlaylistRequestDto addTrackToPlaylistRequestDto = new AddTrackToPlaylistRequestDto(trackId);

        // Mock User, Playlist, Track 객체 생성
        User user = createMockUser(1L, "testUser", "Test User", "testUser@example.com", "testUserProfileImage",
                UserRole.USER, "testUserSocialId");
        Genre genre = createMockGenre(1L, "Test Genre", "testGenreImage");
        Playlist playlist = createMockPlaylist(1L, "Test Playlist", true, false, user);
        Track track = createMockTrack(1L, "Test Track", 120, false, "testTrackUrl", "testTrackImage",
                "testThumbnailImage", user, genre);
        PlaylistTrack playlistTrack = createMockPlaylistTrack(1L, playlist, track);

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));
        given(trackRepository.findById(trackId)).willReturn(Optional.of(track));
        given(playlistTrackRepository.existsByPlaylistAndTrack(playlist, track)).willReturn(false);
        given(playlistTrackRepository.save(any(PlaylistTrack.class))).willReturn(playlistTrack);

        /* When */
        playlistService.addTrackToPlaylist(playlistId, addTrackToPlaylistRequestDto);

        /* Then */
        verify(playlistRepository).findById(playlistId);
        verify(trackRepository).findById(trackId);
        verify(playlistTrackRepository).existsByPlaylistAndTrack(playlist, track);
        verify(playlistTrackRepository).save(any(PlaylistTrack.class));
    }

    private User createMockUser(Long userId, String userName, String displayName, String userEmail, String profileImage,
            UserRole role, String socialId) {
        try {
            Constructor<User> constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            User user = constructor.newInstance();

            setField(user, "userId", userId);
            setField(user, "userName", userName);
            setField(user, "displayName", displayName);
            setField(user, "userEmail", userEmail);
            setField(user, "profileImage", profileImage);
            setField(user, "role", role);
            setField(user, "socialId", socialId);

            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock User", e);
        }
    }

    private Genre createMockGenre(Long id, String genreName, String genreImage) {
        try {
            Constructor<Genre> constructor = Genre.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Genre genre = constructor.newInstance();

            setField(genre, "id", id);
            setField(genre, "genreName", genreName);
            setField(genre, "genreImage", genreImage);

            return genre;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock Genre", e);
        }
    }

    private Track createMockTrack(Long id, String title, int duration, boolean hasLyrics,
            String trackUrl, String trackImage, String thumbnailImage, User user, Genre genre) {
        try {
            Constructor<Track> constructor = Track.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Track track = constructor.newInstance();

            setField(track, "id", id);
            setField(track, "title", title);
            setField(track, "duration", duration);
            setField(track, "hasLyrics", hasLyrics);
            setField(track, "trackUrl", trackUrl);
            setField(track, "trackImage", trackImage);
            setField(track, "thumbnailImage", thumbnailImage);
            setField(track, "user", user);
            setField(track, "genre", genre);

            return track;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock Track", e);
        }
    }

    private TrackDetail createMockTrackDetail(Long id, String prompt) {
        try {
            Constructor<TrackDetail> constructor = TrackDetail.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            TrackDetail trackDetail = constructor.newInstance();

            setField(trackDetail, "id", id);
            setField(trackDetail, "prompt", prompt);

            return trackDetail;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock TrackDetail", e);
        }
    }

    private Playlist createMockPlaylist(Long id, String playlistName, boolean isPublic, boolean isCurated,
            User user) {
        try {
            Constructor<Playlist> constructor = Playlist.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Playlist playlist = constructor.newInstance();

            setField(playlist, "id", id);
            setField(playlist, "playlistName", playlistName);
            setField(playlist, "isPublic", isPublic);
            setField(playlist, "isCurated", isCurated);
            setField(playlist, "user", user);

            return playlist;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock Playlist", e);
        }
    }

    private PlaylistTrack createMockPlaylistTrack(Long id, Playlist playlist, Track track) {
        try {
            Constructor<PlaylistTrack> constructor = PlaylistTrack.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            PlaylistTrack playlistTrack = constructor.newInstance();

            setField(playlistTrack, "id", id);
            setField(playlistTrack, "playlist", playlist);
            setField(playlistTrack, "track", track);

            return playlistTrack;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock PlaylistTrack", e);
        }
    }

    private MyPlaylist createMockMyPlaylist(Long id, Playlist playlist, User user) {
        try {
            Constructor<MyPlaylist> constructor = MyPlaylist.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            MyPlaylist myPlaylist = constructor.newInstance();

            setField(myPlaylist, "id", id);
            setField(myPlaylist, "playlist", playlist);
            setField(myPlaylist, "user", user);

            return myPlaylist;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock MyPlaylist", e);
        }
    }

    private void setField(Object target, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
