package com.example.dreamvalutbackend.domain.playlist.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.CreatePlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.service.PlaylistService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlaylistControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private PlaylistService playlistService;

    @InjectMocks
    private PlaylistController playlistController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(playlistController).build();
    }

    @Test
    @DisplayName("POST /playlists - Unit Success")
    public void createPlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 CreatePlaylistRequestDto 객체 생성
        CreatePlaylistRequestDto createPlaylistRequestDto = CreatePlaylistRequestDto.builder()
                .playlistName("Test Playlist")
                .isPublic(true)
                .build();

        // PlaylistService.createPlaylist() 메소드가 리턴할 CreatePlaylistResponseDto 객체 생성
        CreatePlaylistResponseDto createPlaylistResponseDto = CreatePlaylistResponseDto.builder()
                .playlistId(1L)
                .playlistName("Test Playlist")
                .isPublic(true)
                .isCurated(false)
                .build();

        // PlaylistService.createPlaylist() 메소드 Mocking
        given(playlistService.createPlaylist(any(CreatePlaylistRequestDto.class))).willReturn(createPlaylistResponseDto);

        /* When & Then */
        mockMvc.perform(post("/playlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPlaylistRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playlistId").value(1L))
                .andExpect(jsonPath("$.playlistName").value("Test Playlist"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.isCurated").value(false));
    }
}
