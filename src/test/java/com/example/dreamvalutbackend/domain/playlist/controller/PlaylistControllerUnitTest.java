package com.example.dreamvalutbackend.domain.playlist.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.AddTrackToPlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.UpdatePlaylistNameRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.playlist.controller.response.PlaylistWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.playlist.service.PlaylistService;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackOverviewResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
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
        mockMvc = MockMvcBuilders.standaloneSetup(playlistController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
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
        PlaylistResponseDto createPlaylistResponseDto = PlaylistResponseDto.builder()
                .playlistId(1L)
                .playlistName("Test Playlist")
                .isPublic(true)
                .isCurated(false)
                .build();

        // PlaylistService.createPlaylist() 메소드 Mocking
        given(playlistService.createPlaylist(any(CreatePlaylistRequestDto.class)))
                .willReturn(createPlaylistResponseDto);
        given(playlistService.createPlaylist(any(CreatePlaylistRequestDto.class)))
                .willReturn(createPlaylistResponseDto);

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

    @Test
    @DisplayName("GET /playlists?type=curated - Unit Success")
    public void getCuratedPlaylistsWithTracksOverviewSuccess() throws Exception {
        /* Given */

        // 플레리스트와 트랙들의 Overview 정보를 담은 PlaylistWithTracksOverviewResponseDto 객체 리스트 생성
        Page<PlaylistWithTracksOverviewResponseDto> playlistWithTracksOverviewResponseDtos = new PageImpl<>(List.of(
                new PlaylistWithTracksOverviewResponseDto(1L, "Test Playlist 1", true, true, null, List.of(
                        TrackOverviewResponseDto.builder()
                                .trackId(1L)
                                .title("Track 1")
                                .uploaderName("Artist 1")
                                .thumbnailImage("url1")
                                .build(),
                        TrackOverviewResponseDto.builder()
                                .trackId(2L)
                                .title("Track 2")
                                .uploaderName("Artist 2")
                                .thumbnailImage("url2")
                                .build(),
                        TrackOverviewResponseDto.builder()
                                .trackId(3L)
                                .title("Track 3")
                                .uploaderName("Artist 3")
                                .thumbnailImage("url3")
                                .build())),
                new PlaylistWithTracksOverviewResponseDto(2L, "Test Playlist 2", true, true, null, List.of(
                        TrackOverviewResponseDto.builder()
                                .trackId(4L)
                                .title("Track 4")
                                .uploaderName("Artist 4")
                                .thumbnailImage("url4")
                                .build(),
                        TrackOverviewResponseDto.builder()
                                .trackId(5L)
                                .title("Track 5")
                                .uploaderName("Artist 5")
                                .thumbnailImage("url5")
                                .build(),
                        TrackOverviewResponseDto.builder()
                                .trackId(6L)
                                .title("Track 6")
                                .uploaderName("Artist 6")
                                .thumbnailImage("url6")
                                .build()))),
                PageRequest.of(0, 6), 2);

        // PlaylistService.getPlaylistsWithTracksOverview() 메소드 Mocking
        given(playlistService.getPlaylistsWithTracksOverview(eq("curated"), any(PageRequest.class)))
                .willReturn(playlistWithTracksOverviewResponseDtos);

        /* When & Then */
        mockMvc.perform(get("/playlists?type=curated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playlistId").value(1))
                .andExpect(jsonPath("$.content[0].playlistName").value("Test Playlist 1"))
                .andExpect(jsonPath("$.content[0].isPublic").value(true))
                .andExpect(jsonPath("$.content[0].isCurated").value(true))
                .andExpect(jsonPath("$.content[0].ownerName").isEmpty())
                .andExpect(jsonPath("$.content[0].tracks[0].trackId").value(1))
                .andExpect(jsonPath("$.content[0].tracks[0].title").value("Track 1"))
                .andExpect(jsonPath("$.content[0].tracks[0].uploaderName").value("Artist 1"))
                .andExpect(jsonPath("$.content[0].tracks[0].thumbnailImage").value("url1"))
                .andExpect(jsonPath("$.content[0].tracks[1].trackId").value(2))
                .andExpect(jsonPath("$.content[0].tracks[1].title").value("Track 2"))
                .andExpect(jsonPath("$.content[0].tracks[1].uploaderName").value("Artist 2"))
                .andExpect(jsonPath("$.content[0].tracks[1].thumbnailImage").value("url2"))
                .andExpect(jsonPath("$.content[0].tracks[2].trackId").value(3))
                .andExpect(jsonPath("$.content[0].tracks[2].title").value("Track 3"))
                .andExpect(jsonPath("$.content[0].tracks[2].uploaderName").value("Artist 3"))
                .andExpect(jsonPath("$.content[0].tracks[2].thumbnailImage").value("url3"))
                .andExpect(jsonPath("$.content[1].playlistId").value(2))
                .andExpect(jsonPath("$.content[1].playlistName").value("Test Playlist 2"))
                .andExpect(jsonPath("$.content[1].isPublic").value(true))
                .andExpect(jsonPath("$.content[1].isCurated").value(true))
                .andExpect(jsonPath("$.content[1].ownerName").isEmpty())
                .andExpect(jsonPath("$.content[1].tracks[0].trackId").value(4))
                .andExpect(jsonPath("$.content[1].tracks[0].title").value("Track 4"))
                .andExpect(jsonPath("$.content[1].tracks[0].uploaderName").value("Artist 4"))
                .andExpect(jsonPath("$.content[1].tracks[0].thumbnailImage").value("url4"))
                .andExpect(jsonPath("$.content[1].tracks[1].trackId").value(5))
                .andExpect(jsonPath("$.content[1].tracks[1].title").value("Track 5"))
                .andExpect(jsonPath("$.content[1].tracks[1].uploaderName").value("Artist 5"))
                .andExpect(jsonPath("$.content[1].tracks[1].thumbnailImage").value("url5"))
                .andExpect(jsonPath("$.content[1].tracks[2].trackId").value(6))
                .andExpect(jsonPath("$.content[1].tracks[2].title").value("Track 6"))
                .andExpect(jsonPath("$.content[1].tracks[2].uploaderName").value("Artist 6"))
                .andExpect(jsonPath("$.content[1].tracks[2].thumbnailImage").value("url6"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(6))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.empty").value(false));
    }

    @Test
    @DisplayName("GET /playlists?type=user_created - Unit Success")
    public void getUserCreatedPlaylistsWithTracksOverviewSuccess() throws Exception {
        /* Given */

        // 플레리스트와 트랙들의 Overview 정보를 담은 PlaylistWithTracksOverviewResponseDto 객체 리스트 생성
        Page<PlaylistWithTracksOverviewResponseDto> playlistWithTracksOverviewResponseDtos = new PageImpl<>(List.of(
                new PlaylistWithTracksOverviewResponseDto(1L, "Test Playlist 1", true, false, "Test User", List.of(
                        TrackOverviewResponseDto.builder()
                                .trackId(1L)
                                .title("Track 1")
                                .uploaderName("Artist 1")
                                .thumbnailImage("url1")
                                .build(),
                        TrackOverviewResponseDto.builder()
                                .trackId(2L)
                                .title("Track 2")
                                .uploaderName("Artist 2")
                                .thumbnailImage("url2")
                                .build(),
                        TrackOverviewResponseDto.builder()
                                .trackId(3L)
                                .title("Track 3")
                                .uploaderName("Artist 3")
                                .thumbnailImage("url3")
                                .build())),
                new PlaylistWithTracksOverviewResponseDto(2L, "Test Playlist 2", true, false, "Test User", List.of(
                        TrackOverviewResponseDto.builder()
                                .trackId(4L)
                                .title("Track 4")
                                .uploaderName("Artist 4")
                                .thumbnailImage("url4")
                                .build(),
                        TrackOverviewResponseDto.builder()
                                .trackId(5L)
                                .title("Track 5")
                                .uploaderName("Artist 5")
                                .thumbnailImage("url5")
                                .build(),
                        TrackOverviewResponseDto.builder()
                                .trackId(6L)
                                .title("Track 6")
                                .uploaderName("Artist 6")
                                .thumbnailImage("url6")
                                .build()))),
                PageRequest.of(0, 6), 2);

        // PlaylistService.getPlaylistsWithTracksOverview() 메소드 Mocking
        given(playlistService.getPlaylistsWithTracksOverview(eq("user_created"), any(PageRequest.class)))
                .willReturn(playlistWithTracksOverviewResponseDtos);

        /* When & Then */
        mockMvc.perform(get("/playlists?type=user_created"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playlistId").value(1))
                .andExpect(jsonPath("$.content[0].playlistName").value("Test Playlist 1"))
                .andExpect(jsonPath("$.content[0].isPublic").value(true))
                .andExpect(jsonPath("$.content[0].isCurated").value(false))
                .andExpect(jsonPath("$.content[0].ownerName").value("Test User"))
                .andExpect(jsonPath("$.content[0].tracks[0].trackId").value(1))
                .andExpect(jsonPath("$.content[0].tracks[0].title").value("Track 1"))
                .andExpect(jsonPath("$.content[0].tracks[0].uploaderName").value("Artist 1"))
                .andExpect(jsonPath("$.content[0].tracks[0].thumbnailImage").value("url1"))
                .andExpect(jsonPath("$.content[0].tracks[1].trackId").value(2))
                .andExpect(jsonPath("$.content[0].tracks[1].title").value("Track 2"))
                .andExpect(jsonPath("$.content[0].tracks[1].uploaderName").value("Artist 2"))
                .andExpect(jsonPath("$.content[0].tracks[1].thumbnailImage").value("url2"))
                .andExpect(jsonPath("$.content[0].tracks[2].trackId").value(3))
                .andExpect(jsonPath("$.content[0].tracks[2].title").value("Track 3"))
                .andExpect(jsonPath("$.content[0].tracks[2].uploaderName").value("Artist 3"))
                .andExpect(jsonPath("$.content[0].tracks[2].thumbnailImage").value("url3"))
                .andExpect(jsonPath("$.content[1].playlistId").value(2))
                .andExpect(jsonPath("$.content[1].playlistName").value("Test Playlist 2"))
                .andExpect(jsonPath("$.content[1].isPublic").value(true))
                .andExpect(jsonPath("$.content[1].isCurated").value(false))
                .andExpect(jsonPath("$.content[1].ownerName").value("Test User"))
                .andExpect(jsonPath("$.content[1].tracks[0].trackId").value(4))
                .andExpect(jsonPath("$.content[1].tracks[0].title").value("Track 4"))
                .andExpect(jsonPath("$.content[1].tracks[0].uploaderName").value("Artist 4"))
                .andExpect(jsonPath("$.content[1].tracks[0].thumbnailImage").value("url4"))
                .andExpect(jsonPath("$.content[1].tracks[1].trackId").value(5))
                .andExpect(jsonPath("$.content[1].tracks[1].title").value("Track 5"))
                .andExpect(jsonPath("$.content[1].tracks[1].uploaderName").value("Artist 5"))
                .andExpect(jsonPath("$.content[1].tracks[1].thumbnailImage").value("url5"))
                .andExpect(jsonPath("$.content[1].tracks[2].trackId").value(6))
                .andExpect(jsonPath("$.content[1].tracks[2].title").value("Track 6"))
                .andExpect(jsonPath("$.content[1].tracks[2].uploaderName").value("Artist 6"))
                .andExpect(jsonPath("$.content[1].tracks[2].thumbnailImage").value("url6"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(6))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.empty").value(false));
    }

    @Test
    @DisplayName("GET /playlists/{playlist_id} - Unit Success")
    public void getPlaylistWithTracksSuccess() throws Exception {
        /* Given */

        // 요청할 playlist ID
        Long playlistId = 1L;

        // TrackResponseDto 객체 생성
        TrackResponseDto trackResponseDto1 = TrackResponseDto.builder()
                .trackId(1L)
                .title("Dummy Title 1")
                .uploaderName("Dummy Uploader 1")
                .duration(300)
                .hasLyrics(true)
                .trackUrl("http://dummyurl1.com")
                .trackImage("http://dummyimage1.com")
                .thumbnailImage("http://dummythumbnail1.com")
                .prompt("Dummy Prompt 1")
                .build();
        TrackResponseDto trackResponseDto2 = TrackResponseDto.builder()
                .trackId(2L)
                .title("Dummy Title 2")
                .uploaderName("Dummy Uploader 2")
                .duration(200)
                .hasLyrics(false)
                .trackUrl("http://dummyurl2.com")
                .trackImage("http://dummyimage2.com")
                .thumbnailImage("http://dummythumbnail2.com")
                .prompt("Dummy Prompt 2")
                .build();

        // TrackResponseDto 객체 리스트
        List<TrackResponseDto> trackResponseDtoList = Arrays.asList(trackResponseDto1, trackResponseDto2);

        // Page 객체 생성
        Page<TrackResponseDto> dummyTracks = new PageImpl<>(trackResponseDtoList, PageRequest.of(0, 10),
                trackResponseDtoList.size());

        // PlaylistWithTracksResponseDto 객체 생성
        PlaylistWithTracksResponseDto playlistWithTracksResponseDto = PlaylistWithTracksResponseDto.builder()
                .playlistId(playlistId)
                .playlistName("Test Playlist")
                .isPublic(true)
                .isCurated(false)
                .ownerName("Test User")
                .tracks(dummyTracks)
                .build();

        // PlaylistService.getPlaylistWithTracks() 메소드 Mocking
        given(playlistService.getPlaylistWithTracks(eq(playlistId), any(PageRequest.class), any(long.class)))
                .willReturn(playlistWithTracksResponseDto);

        /* When & Then */
        mockMvc.perform(get("/playlists/{playlist_id}", playlistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playlistId").value(1))
                .andExpect(jsonPath("$.playlistName").value("Test Playlist"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.isCurated").value(false))
                .andExpect(jsonPath("$.ownerName").value("Test User"))
                .andExpect(jsonPath("$.tracks.content[0].trackId").value(1))
                .andExpect(jsonPath("$.tracks.content[0].title").value("Dummy Title 1"))
                .andExpect(jsonPath("$.tracks.content[0].uploaderName").value("Dummy Uploader 1"))
                .andExpect(jsonPath("$.tracks.content[0].duration").value(300))
                .andExpect(jsonPath("$.tracks.content[0].hasLyrics").value(true))
                .andExpect(jsonPath("$.tracks.content[0].trackUrl").value("http://dummyurl1.com"))
                .andExpect(jsonPath("$.tracks.content[0].trackImage").value("http://dummyimage1.com"))
                .andExpect(jsonPath("$.tracks.content[0].thumbnailImage").value("http://dummythumbnail1.com"))
                .andExpect(jsonPath("$.tracks.content[0].prompt").value("Dummy Prompt 1"))
                .andExpect(jsonPath("$.tracks.content[1].trackId").value(2))
                .andExpect(jsonPath("$.tracks.content[1].title").value("Dummy Title 2"))
                .andExpect(jsonPath("$.tracks.content[1].uploaderName").value("Dummy Uploader 2"))
                .andExpect(jsonPath("$.tracks.content[1].duration").value(200))
                .andExpect(jsonPath("$.tracks.content[1].hasLyrics").value(false))
                .andExpect(jsonPath("$.tracks.content[1].trackUrl").value("http://dummyurl2.com"))
                .andExpect(jsonPath("$.tracks.content[1].trackImage").value("http://dummyimage2.com"))
                .andExpect(jsonPath("$.tracks.content[1].thumbnailImage").value("http://dummythumbnail2.com"))
                .andExpect(jsonPath("$.tracks.content[1].prompt").value("Dummy Prompt 2"))
                .andExpect(jsonPath("$.tracks.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.tracks.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.tracks.pageable.sort.empty").value(true))
                .andExpect(jsonPath("$.tracks.pageable.sort.unsorted").value(true))
                .andExpect(jsonPath("$.tracks.pageable.sort.sorted").value(false))
                .andExpect(jsonPath("$.tracks.pageable.offset").value(0))
                .andExpect(jsonPath("$.tracks.pageable.paged").value(true))
                .andExpect(jsonPath("$.tracks.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.tracks.totalPages").value(1))
                .andExpect(jsonPath("$.tracks.last").value(true))
                .andExpect(jsonPath("$.tracks.totalElements").value(2))
                .andExpect(jsonPath("$.tracks.size").value(10))
                .andExpect(jsonPath("$.tracks.first").value(true))
                .andExpect(jsonPath("$.tracks.numberOfElements").value(2))
                .andExpect(jsonPath("$.tracks.empty").value(false));
    }

    @Test
    @DisplayName("PATCH /playlists/{playlist_id} - Unit Success")
    public void updatePlaylistNameSuccess() throws Exception {
        /* Given */

        // 요청할 playlist ID
        Long playlistId = 1L;

        // 요청할 UpdatePlaylistNameRequestDto 객체 생성
        UpdatePlaylistNameRequestDto updatePlaylistNameRequestDto = new UpdatePlaylistNameRequestDto(
                "Updated Playlist Name");

        // PlaylistService.updatePlaylistName() 메소드가 리턴할 PlaylistResponseDto 객체 생성
        PlaylistResponseDto updatePlaylistNameResponseDto = PlaylistResponseDto.builder()
                .playlistId(1L)
                .playlistName("Updated Playlist Name")
                .isPublic(true)
                .isCurated(false)
                .build();

        // PlaylistService.updatePlaylistName() 메소드 Mocking
        given(playlistService.updatePlaylistName(eq(playlistId), any(UpdatePlaylistNameRequestDto.class)))
                .willReturn(updatePlaylistNameResponseDto);

        /* When & Then */
        mockMvc.perform(patch("/playlists/{playlist_id}", playlistId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePlaylistNameRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playlistId").value(1L))
                .andExpect(jsonPath("$.playlistName").value("Updated Playlist Name"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.isCurated").value(false));
    }

    @Test
    @DisplayName("DELETE /playlists/{playlist_id} - Unit Success")
    public void deletePlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 playlist ID
        Long playlistId = 1L;

        // PlaylistService.deletePlaylist() 메소드 Mocking
        willDoNothing().given(playlistService).deletePlaylist(playlistId);

        /* When & Then */
        mockMvc.perform(delete("/playlists/{playlist_id}", playlistId))
                .andExpect(status().isNoContent());

        // PlaylistService.deletePlaylist() 메소드가 정상적으로 호출되었는지 확인
        verify(playlistService).deletePlaylist(playlistId);
    }

    @Test
    @DisplayName("POST /playlists/{playlist_id}/tracks - Unit Success")
    public void addTrackToPlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 playlist ID 및 track ID
        Long playlistId = 1L;
        Long trackId = 1L;

        // 요청할 AddTrackToPlaylistRequestDto 객체 생성
        AddTrackToPlaylistRequestDto addTrackToPlaylistRequestDto = new AddTrackToPlaylistRequestDto(trackId);

        // AddTrackToPlaylistRequestDto 객체를 JSON 형태로 변환
        String requestContent = objectMapper.writeValueAsString(addTrackToPlaylistRequestDto);

        // PlaylistService.addTrackToPlaylist() 메소드 Mocking
        willDoNothing().given(playlistService).addTrackToPlaylist(eq(playlistId),
                any(AddTrackToPlaylistRequestDto.class));

        /* When & Then */
        mockMvc.perform(post("/playlists/{playlist_id}/tracks", playlistId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isOk());

        // PlaylistService.addTrackToPlaylist() 메소드가 정상적으로 호출되었는지 확인
        verify(playlistService).addTrackToPlaylist(eq(playlistId), any(AddTrackToPlaylistRequestDto.class));
    }

    @Test
    @DisplayName("DELETE /playlists/{playlist_id}/tracks/{track_id} - Unit Success")
    public void deleteTrackFromPlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 playlist ID 및 track ID
        Long playlistId = 1L;
        Long trackId = 1L;

        // PlaylistService.deleteTrackFromPlaylist() 메소드 Mocking
        willDoNothing().given(playlistService).deleteTrackFromPlaylist(eq(playlistId), eq(trackId));

        /* When & Then */
        mockMvc.perform(delete("/playlists/{playlist_id}/tracks/{track_id}", playlistId, trackId))
                .andExpect(status().isNoContent());

        // PlaylistService.deleteTrackFromPlaylist() 메소드가 정상적으로 호출되었는지 확인
        verify(playlistService).deleteTrackFromPlaylist(eq(playlistId), eq(trackId));
    }

    @Test
    @DisplayName("POST /playlists/{playlist_id}/follow - Unit Success")
    public void followPlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 playlist ID
        Long playlistId = 1L;

        // PlaylistService.followPlaylist() 메소드 Mocking
        willDoNothing().given(playlistService).followPlaylist(playlistId);

        /* When & Then */
        mockMvc.perform(post("/playlists/{playlist_id}/follow", playlistId))
                .andExpect(status().isOk());

        // PlaylistService.followPlaylist() 메소드가 정상적으로 호출되었는지 확인
        verify(playlistService).followPlaylist(playlistId);
    }

    @Test
    @DisplayName("DELETE /playlists/{playlist_id}/follow - Unit Success")
    public void unfollowPlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 playlist ID
        Long playlistId = 1L;

        // PlaylistService.unfollowPlaylist() 메소드 Mocking
        willDoNothing().given(playlistService).unfollowPlaylist(playlistId);

        /* When & Then */
        mockMvc.perform(delete("/playlists/{playlist_id}/follow", playlistId))
                .andExpect(status().isNoContent());

        // PlaylistService.unfollowPlaylist() 메소드가 정상적으로 호출되었는지 확인
        verify(playlistService).unfollowPlaylist(playlistId);
    }
}
