package com.example.dreamvalutbackend.domain.playlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.CreatePlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.domain.MyPlaylist;
import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.playlist.repository.MyPlaylistRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;

public class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private MyPlaylistRepository myPlaylistRepository;
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
        CreatePlaylistResponseDto createPlaylistResponseDto = playlistService.createPlaylist(createPlaylistRequestDto);

        /* Then */
        assertThat(createPlaylistResponseDto.getPlaylistId()).isEqualTo(playlist.getId());
        assertThat(createPlaylistResponseDto.getPlaylistName()).isEqualTo(playlist.getPlaylistName());
        assertThat(createPlaylistResponseDto.getIsPublic()).isEqualTo(playlist.getIsPublic());
        assertThat(createPlaylistResponseDto.getIsCurated()).isEqualTo(playlist.getIsCurated());
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
