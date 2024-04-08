package com.example.dreamvalutbackend.domain.playlist.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.repository.MyPlaylistRepository;
import com.example.dreamvalutbackend.domain.playlist.repository.PlaylistRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private MyPlaylistRepository myPlaylistRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userName("testUserName")
                .displayName("testDisplayName")
                .userEmail("testEmail")
                .profileImage("testProfileImage")
                .role(UserRole.USER)
                .socialId("testSocialId")
                .build();
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        myPlaylistRepository.deleteAll();
        playlistRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /playlists - Integration Success")
    @Transactional
    void createPlaylistSuccess() throws Exception {
        /* Given */

        CreatePlaylistRequestDto createPlaylistRequestDto = CreatePlaylistRequestDto.builder()
                .playlistName("Test Playlist")
                .isPublic(true)
                .build();

        String requestContent = objectMapper.writeValueAsString(createPlaylistRequestDto);

        /* When & Then */
        mockMvc.perform(post("/playlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playlistId").exists())
                .andExpect(jsonPath("$.playlistName").value("Test Playlist"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.isCurated").value(false));
    }
}
