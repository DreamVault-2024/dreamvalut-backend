package com.example.dreamvalutbackend.domain.playlist.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.playlist.controller.request.AddTrackToPlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.UpdatePlaylistNameRequestDto;
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
    private GenreRepository genreRepository;
    @Autowired
    private TrackRepository trackRepository;
    @Autowired
    private TrackDetailRepository trackDetailRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;
    @Autowired
    private MyPlaylistRepository myPlaylistRepository;

    private User user;
    private Genre genre;
    private List<Playlist> userCreatedPlaylists;
    private List<Playlist> curatedPlaylists;
    private List<Track> tracks;
    private List<TrackDetail> trackDetails;
    private List<PlaylistTrack> userCreatedPlaylistTracks;
    private List<PlaylistTrack> curatedPlaylistTracks;
    private MyPlaylist myPlaylist;

    @BeforeEach
    void setUp() {
        // 기본 유저 데이터 생성
        user = userRepository.save(createUser("testUserName", "testDisplayName", "testEmail", "testProfileImage",
                UserRole.USER, "testSocialId"));

        // 기본 장르 데이터 생성
        genre = genreRepository.save(createGenre("Test Genre", "testGenreImage"));

        // 기본 트랙 데이터 생성
        tracks = List.of(
                trackRepository.save(createTrack("Test Track 1", 100, false, "testTrackUrl1", "testTrackImage1",
                        "testThumbnailImage1", user, genre)),
                trackRepository.save(createTrack("Test Track 2", 200, false, "testTrackUrl2", "testTrackImage2",
                        "testThumbnailImage2", user, genre)),
                trackRepository.save(createTrack("Test Track 3", 300, false, "testTrackUrl3", "testTrackImage3",
                        "testThumbnailImage3", user, genre)),
                trackRepository.save(createTrack("Test Track 4", 400, false, "testTrackUrl4", "testTrackImage4",
                        "testThumbnailImage4", user, genre)));

        // 기본 트랙 상세 데이터 생성
        trackDetails = List.of(
                trackDetailRepository.save(createTrackDetail(tracks.get(0), "Test Prompt 1")),
                trackDetailRepository.save(createTrackDetail(tracks.get(1), "Test Prompt 2")),
                trackDetailRepository.save(createTrackDetail(tracks.get(2), "Test Prompt 3")));

        // 유저 생성 플레이리스트 데이터 생성
        userCreatedPlaylists = List.of(
                playlistRepository.save(createPlaylist("Test Playlist 1", true, false, user)),
                playlistRepository.save(createPlaylist("Test Playlist 2", true, false, user)));

        // 큐레이션 플레이리스트 데이터 생성
        curatedPlaylists = List.of(
                playlistRepository.save(createPlaylist("Curated Playlist 1", true, true, null)),
                playlistRepository.save(createPlaylist("Curated Playlist 2", true, true, null)));

        // 유저 생성 플레이리스트 트랙 데이터 생성
        userCreatedPlaylistTracks = List.of(
                playlistTrackRepository.save(createPlaylistTrack(userCreatedPlaylists.get(0), tracks.get(0))),
                playlistTrackRepository.save(createPlaylistTrack(userCreatedPlaylists.get(0), tracks.get(1))),
                playlistTrackRepository.save(createPlaylistTrack(userCreatedPlaylists.get(0), tracks.get(2))),
                playlistTrackRepository.save(createPlaylistTrack(userCreatedPlaylists.get(1), tracks.get(1))),
                playlistTrackRepository.save(createPlaylistTrack(userCreatedPlaylists.get(1), tracks.get(2))),
                playlistTrackRepository.save(createPlaylistTrack(userCreatedPlaylists.get(1), tracks.get(3))));

        // 큐레이션 플레이리스트 트랙 데이터 생성
        curatedPlaylistTracks = List.of(
                playlistTrackRepository.save(createPlaylistTrack(curatedPlaylists.get(0), tracks.get(0))),
                playlistTrackRepository.save(createPlaylistTrack(curatedPlaylists.get(0), tracks.get(1))),
                playlistTrackRepository.save(createPlaylistTrack(curatedPlaylists.get(0), tracks.get(2))),
                playlistTrackRepository.save(createPlaylistTrack(curatedPlaylists.get(1), tracks.get(1))),
                playlistTrackRepository.save(createPlaylistTrack(curatedPlaylists.get(1), tracks.get(2))),
                playlistTrackRepository.save(createPlaylistTrack(curatedPlaylists.get(1), tracks.get(3))));

        // 내 플레이리스트 데이터 생성
        myPlaylist = myPlaylistRepository.save(MyPlaylist.builder()
                .playlist(curatedPlaylists.get(0))
                .user(user)
                .build());
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

    @Test
    @DisplayName("GET /playlists?type=curated - Integration Success")
    @Transactional
    void getCuratedPlaylistsWithTracksOverviewSuccess() throws Exception {
        /* Given */

        // 요청할 플레이리스트 타입
        String type = "curated";

        /* When & Then */
        mockMvc.perform(get("/playlists").param("type", type).param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playlistId").value(curatedPlaylists.get(1).getId()))
                .andExpect(jsonPath("$.content[0].playlistName").value(curatedPlaylists.get(1).getPlaylistName()))
                .andExpect(jsonPath("$.content[0].isPublic").value(curatedPlaylists.get(1).getIsPublic()))
                .andExpect(jsonPath("$.content[0].isCurated").value(curatedPlaylists.get(1).getIsCurated()))
                .andExpect(jsonPath("$.content[0].ownerName").isEmpty())
                .andExpect(jsonPath("$.content[0].tracks[0].trackId").value(tracks.get(3).getId()))
                .andExpect(jsonPath("$.content[0].tracks[0].title").value(tracks.get(3).getTitle()))
                .andExpect(
                        jsonPath("$.content[0].tracks[0].uploaderName").value(tracks.get(3).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[0].tracks[0].thumbnailImage").value(tracks.get(3).getThumbnailImage()))
                .andExpect(jsonPath("$.content[0].tracks[1].trackId").value(tracks.get(2).getId()))
                .andExpect(jsonPath("$.content[0].tracks[1].title").value(tracks.get(2).getTitle()))
                .andExpect(
                        jsonPath("$.content[0].tracks[1].uploaderName").value(tracks.get(2).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[0].tracks[1].thumbnailImage").value(tracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.content[0].tracks[2].trackId").value(tracks.get(1).getId()))
                .andExpect(jsonPath("$.content[0].tracks[2].title").value(tracks.get(1).getTitle()))
                .andExpect(
                        jsonPath("$.content[0].tracks[2].uploaderName").value(tracks.get(1).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[0].tracks[2].thumbnailImage").value(tracks.get(1).getThumbnailImage()))
                .andExpect(jsonPath("$.content[1].playlistId").value(curatedPlaylists.get(0).getId()))
                .andExpect(jsonPath("$.content[1].playlistName").value(curatedPlaylists.get(0).getPlaylistName()))
                .andExpect(jsonPath("$.content[1].isPublic").value(curatedPlaylists.get(0).getIsPublic()))
                .andExpect(jsonPath("$.content[1].isCurated").value(curatedPlaylists.get(0).getIsCurated()))
                .andExpect(jsonPath("$.content[1].ownerName").isEmpty())
                .andExpect(jsonPath("$.content[1].tracks[0].trackId").value(tracks.get(2).getId()))
                .andExpect(jsonPath("$.content[1].tracks[0].title").value(tracks.get(2).getTitle()))
                .andExpect(
                        jsonPath("$.content[1].tracks[0].uploaderName").value(tracks.get(2).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[1].tracks[0].thumbnailImage").value(tracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.content[1].tracks[1].trackId").value(tracks.get(1).getId()))
                .andExpect(jsonPath("$.content[1].tracks[1].title").value(tracks.get(1).getTitle()))
                .andExpect(
                        jsonPath("$.content[1].tracks[1].uploaderName").value(tracks.get(1).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[1].tracks[1].thumbnailImage").value(tracks.get(1).getThumbnailImage()))
                .andExpect(jsonPath("$.content[1].tracks[2].trackId").value(tracks.get(0).getId()))
                .andExpect(jsonPath("$.content[1].tracks[2].title").value(tracks.get(0).getTitle()))
                .andExpect(
                        jsonPath("$.content[1].tracks[2].uploaderName").value(tracks.get(0).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[1].tracks[2].thumbnailImage").value(tracks.get(0).getThumbnailImage()))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(2))
                .andExpect(jsonPath("$.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.sort.empty").value(false))
                .andExpect(jsonPath("$.sort.unsorted").value(false))
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.empty").value(false));
    }

    @Test
    @DisplayName("GET /playlists?type=user_created - Integration Success")
    @Transactional
    void getUserCreatedPlaylistsWithTracksOverviewSuccess() throws Exception {
        /* Given */

        // 요청할 플레이리스트 타입
        String type = "user_created";

        /* When & Then */
        mockMvc.perform(get("/playlists").param("type", type).param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playlistId").value(userCreatedPlaylists.get(1).getId()))
                .andExpect(jsonPath("$.content[0].playlistName").value(userCreatedPlaylists.get(1).getPlaylistName()))
                .andExpect(jsonPath("$.content[0].isPublic").value(userCreatedPlaylists.get(1).getIsPublic()))
                .andExpect(jsonPath("$.content[0].isCurated").value(userCreatedPlaylists.get(1).getIsCurated()))
                .andExpect(jsonPath("$.content[0].ownerName")
                        .value(userCreatedPlaylists.get(1).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[0].tracks[0].trackId").value(tracks.get(3).getId()))
                .andExpect(jsonPath("$.content[0].tracks[0].title").value(tracks.get(3).getTitle()))
                .andExpect(
                        jsonPath("$.content[0].tracks[0].uploaderName").value(tracks.get(3).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[0].tracks[0].thumbnailImage").value(tracks.get(3).getThumbnailImage()))
                .andExpect(jsonPath("$.content[0].tracks[1].trackId").value(tracks.get(2).getId()))
                .andExpect(jsonPath("$.content[0].tracks[1].title").value(tracks.get(2).getTitle()))
                .andExpect(
                        jsonPath("$.content[0].tracks[1].uploaderName").value(tracks.get(2).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[0].tracks[1].thumbnailImage").value(tracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.content[0].tracks[2].trackId").value(tracks.get(1).getId()))
                .andExpect(jsonPath("$.content[0].tracks[2].title").value(tracks.get(1).getTitle()))
                .andExpect(
                        jsonPath("$.content[0].tracks[2].uploaderName").value(tracks.get(1).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[0].tracks[2].thumbnailImage").value(tracks.get(1).getThumbnailImage()))
                .andExpect(jsonPath("$.content[1].playlistId").value(userCreatedPlaylists.get(0).getId()))
                .andExpect(jsonPath("$.content[1].playlistName").value(userCreatedPlaylists.get(0).getPlaylistName()))
                .andExpect(jsonPath("$.content[1].isPublic").value(userCreatedPlaylists.get(0).getIsPublic()))
                .andExpect(jsonPath("$.content[1].isCurated").value(userCreatedPlaylists.get(0).getIsCurated()))
                .andExpect(jsonPath("$.content[1].ownerName")
                        .value(userCreatedPlaylists.get(0).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[1].tracks[0].trackId").value(tracks.get(2).getId()))
                .andExpect(jsonPath("$.content[1].tracks[0].title").value(tracks.get(2).getTitle()))
                .andExpect(
                        jsonPath("$.content[1].tracks[0].uploaderName").value(tracks.get(2).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[1].tracks[0].thumbnailImage").value(tracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.content[1].tracks[1].trackId").value(tracks.get(1).getId()))
                .andExpect(jsonPath("$.content[1].tracks[1].title").value(tracks.get(1).getTitle()))
                .andExpect(
                        jsonPath("$.content[1].tracks[1].uploaderName").value(tracks.get(1).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[1].tracks[1].thumbnailImage").value(tracks.get(1).getThumbnailImage()))
                .andExpect(jsonPath("$.content[1].tracks[2].trackId").value(tracks.get(0).getId()))
                .andExpect(jsonPath("$.content[1].tracks[2].title").value(tracks.get(0).getTitle()))
                .andExpect(
                        jsonPath("$.content[1].tracks[2].uploaderName").value(tracks.get(0).getUser().getDisplayName()))
                .andExpect(jsonPath("$.content[1].tracks[2].thumbnailImage").value(tracks.get(0).getThumbnailImage()))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(2))
                .andExpect(jsonPath("$.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.sort.empty").value(false))
                .andExpect(jsonPath("$.sort.unsorted").value(false))
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.empty").value(false));
    }

    @Test
    @DisplayName("GET /playlists/{playlistId} - Integration Success")
    @Transactional
    void getPlaylistWithTracksSuccess() throws Exception {
        /* Given */

        // 요청할 플레이리스트 ID
        Long playlistId = userCreatedPlaylists.get(0).getId();

        /* When & Then */
        mockMvc.perform(get("/playlists/{playlistId}", playlistId).param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playlistId").value(playlistId))
                .andExpect(jsonPath("$.playlistName").value(userCreatedPlaylists.get(0).getPlaylistName()))
                .andExpect(jsonPath("$.isPublic").value(userCreatedPlaylists.get(0).getIsPublic()))
                .andExpect(jsonPath("$.isCurated").value(userCreatedPlaylists.get(0).getIsCurated()))
                .andExpect(jsonPath("$.ownerName").value(userCreatedPlaylists.get(0).getUser().getDisplayName()))
                .andExpect(jsonPath("$.tracks.content[0].trackId").value(tracks.get(2).getId()))
                .andExpect(jsonPath("$.tracks.content[0].title").value(tracks.get(2).getTitle()))
                .andExpect(jsonPath("$.tracks.content[0].uploaderName").value(tracks.get(2).getUser().getDisplayName()))
                .andExpect(jsonPath("$.tracks.content[0].duration").value(tracks.get(2).getDuration()))
                .andExpect(jsonPath("$.tracks.content[0].hasLyrics").value(tracks.get(2).getHasLyrics()))
                .andExpect(jsonPath("$.tracks.content[0].trackUrl").value(tracks.get(2).getTrackUrl()))
                .andExpect(jsonPath("$.tracks.content[0].trackImage").value(tracks.get(2).getTrackImage()))
                .andExpect(jsonPath("$.tracks.content[0].thumbnailImage").value(tracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.tracks.content[0].prompt").value(trackDetails.get(2).getPrompt()))
                .andExpect(jsonPath("$.tracks.content[1].trackId").value(tracks.get(1).getId()))
                .andExpect(jsonPath("$.tracks.content[1].title").value(tracks.get(1).getTitle()))
                .andExpect(jsonPath("$.tracks.content[1].uploaderName").value(tracks.get(1).getUser().getDisplayName()))
                .andExpect(jsonPath("$.tracks.content[1].duration").value(tracks.get(1).getDuration()))
                .andExpect(jsonPath("$.tracks.content[1].hasLyrics").value(tracks.get(1).getHasLyrics()))
                .andExpect(jsonPath("$.tracks.content[1].trackUrl").value(tracks.get(1).getTrackUrl()))
                .andExpect(jsonPath("$.tracks.content[1].trackImage").value(tracks.get(1).getTrackImage()))
                .andExpect(jsonPath("$.tracks.content[1].thumbnailImage").value(tracks.get(1).getThumbnailImage()))
                .andExpect(jsonPath("$.tracks.content[1].prompt").value(trackDetails.get(1).getPrompt()))
                .andExpect(jsonPath("$.tracks.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.tracks.pageable.pageSize").value(2))
                .andExpect(jsonPath("$.tracks.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.tracks.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.tracks.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.tracks.last").value(false))
                .andExpect(jsonPath("$.tracks.totalPages").value(2))
                .andExpect(jsonPath("$.tracks.totalElements").value(3))
                .andExpect(jsonPath("$.tracks.sort.empty").value(false))
                .andExpect(jsonPath("$.tracks.sort.sorted").value(true))
                .andExpect(jsonPath("$.tracks.sort.unsorted").value(false))
                .andExpect(jsonPath("$.tracks.size").value(2))
                .andExpect(jsonPath("$.tracks.number").value(0))
                .andExpect(jsonPath("$.tracks.first").value(true))
                .andExpect(jsonPath("$.tracks.numberOfElements").value(2))
                .andExpect(jsonPath("$.tracks.empty").value(false));
    }

    @Test
    @DisplayName("PATCH /playlists/{playlistId} - Integration Success")
    @Transactional
    void updatePlaylistNameSuccess() throws Exception {
        /* Given */

        // 요청할 플레이리스트 ID
        Long playlistId = userCreatedPlaylists.get(0).getId();
        UpdatePlaylistNameRequestDto updatePlaylistNameRequestDto = new UpdatePlaylistNameRequestDto(
                "Updated Playlist Name");
        String requestContent = objectMapper.writeValueAsString(updatePlaylistNameRequestDto);

        /* When & Then */
        mockMvc.perform(patch("/playlists/{playlistId}", playlistId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playlistId").value(playlistId))
                .andExpect(jsonPath("$.playlistName").value("Updated Playlist Name"))
                .andExpect(jsonPath("$.isPublic").value(userCreatedPlaylists.get(0).getIsPublic()))
                .andExpect(jsonPath("$.isCurated").value(userCreatedPlaylists.get(0).getIsCurated()));
    }

    @Test
    @DisplayName("DELETE /playlists/{playlistId} - Integration Success")
    @Transactional
    void deletePlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 플레이리스트 ID
        Long playlistId = userCreatedPlaylists.get(0).getId();

        /* When & Then */
        mockMvc.perform(delete("/playlists/{playlistId}", playlistId))
                .andExpect(status().isNoContent());

        // 삭제된 플레이리스트 확인
        assertThat(playlistRepository.findById(playlistId)).isEmpty();
    }

    @Test
    @DisplayName("POST /playlists/{playlistId}/tracks - Integration Success")
    @Transactional
    void addTrackToPlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 playlistId와 trackId
        Long playlistId = userCreatedPlaylists.get(0).getId();
        Long trackId = tracks.get(3).getId();

        // Request Body
        AddTrackToPlaylistRequestDto addTrackToPlaylistRequestDto = new AddTrackToPlaylistRequestDto(trackId);
        String requestContent = objectMapper.writeValueAsString(addTrackToPlaylistRequestDto);

        /* When & Then */
        mockMvc.perform(post("/playlists/{playlistId}/tracks", playlistId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isOk());

        // 플레이리스트에 트랙 추가 확인
        assertThat(playlistTrackRepository.existsByPlaylistAndTrack(userCreatedPlaylists.get(0), tracks.get(3)))
                .isTrue();
    }

    @Test
    @DisplayName("DELETE /playlists/{playlistId}/tracks/{trackId} - Integration Success")
    @Transactional
    void deleteTrackFromPlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 playlistId와 trackId
        Long playlistId = userCreatedPlaylists.get(0).getId();
        Long trackId = tracks.get(0).getId();

        /* When & Then */
        mockMvc.perform(delete("/playlists/{playlistId}/tracks/{trackId}", playlistId, trackId))
                .andExpect(status().isNoContent());

        // 플레이리스트에서 트랙 삭제 확인
        assertThat(playlistTrackRepository.existsByPlaylistAndTrack(userCreatedPlaylists.get(0), tracks.get(0)))
                .isFalse();
    }

    @Test
    @DisplayName("POST /playlists/{playlistId}/follow - Integration Success")
    @Transactional
    void followPlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 playlistId
        Long playlistId = curatedPlaylists.get(1).getId();

        /* When & Then */
        mockMvc.perform(post("/playlists/{playlistId}/follow", playlistId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /playlists/{playlistId}/follow - Integration Success")
    @Transactional
    void unfollowPlaylistSuccess() throws Exception {
        /* Given */

        // 요청할 playlistId
        Long playlistId = curatedPlaylists.get(0).getId();

        /* When & Then */
        mockMvc.perform(delete("/playlists/{playlistId}/follow", playlistId))
                .andExpect(status().isNoContent());
    }

    private User createUser(String userName, String displayName, String userEmail, String profileImage, UserRole role,
            String socialId) {
        return User.builder()
                .userName(userName)
                .displayName(displayName)
                .userEmail(userEmail)
                .profileImage(profileImage)
                .role(role)
                .socialId(socialId)
                .build();
    }

    private Genre createGenre(String genreName, String genreImage) {
        return Genre.builder()
                .genreName(genreName)
                .genreImage(genreImage)
                .build();
    }

    private Track createTrack(String title, Integer duration, Boolean hasLyrics, String trackUrl, String trackImage,
            String thumbnailImage, User user, Genre genre) {
        return Track.builder()
                .title(title)
                .duration(duration)
                .hasLyrics(hasLyrics)
                .trackUrl(trackUrl)
                .trackImage(trackImage)
                .thumbnailImage(thumbnailImage)
                .user(user)
                .genre(genre)
                .build();
    }

    private TrackDetail createTrackDetail(Track track, String prompt) {
        return TrackDetail.builder()
                .track(track)
                .prompt(prompt)
                .build();
    }

    private Playlist createPlaylist(String playlistName, Boolean isPublic, Boolean isCurated, User user) {
        return Playlist.builder()
                .playlistName(playlistName)
                .isPublic(isPublic)
                .isCurated(isCurated)
                .user(user)
                .build();
    }

    private PlaylistTrack createPlaylistTrack(Playlist playlist, Track track) {
        return PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .build();
    }
}
