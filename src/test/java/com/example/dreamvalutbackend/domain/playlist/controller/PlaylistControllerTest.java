package com.example.dreamvalutbackend.domain.playlist.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.example.dreamvalutbackend.domain.playlist.controller.request.CreatePlaylistRequestDto;
import com.example.dreamvalutbackend.domain.playlist.controller.request.UpdatePlaylistNameRequestDto;
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
    private Playlist playlist;
    private List<Track> tracks;
    private List<TrackDetail> trackDetails;
    private List<PlaylistTrack> playlistTracks;

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
                        "testThumbnailImage3", user, genre)));

        // 기본 트랙 상세 데이터 생성
        trackDetails = List.of(
                trackDetailRepository.save(createTrackDetail(tracks.get(0), "Test Prompt 1")),
                trackDetailRepository.save(createTrackDetail(tracks.get(1), "Test Prompt 2")),
                trackDetailRepository.save(createTrackDetail(tracks.get(2), "Test Prompt 3")));

        // 기본 플레이리스트 데이터 생성
        playlist = playlistRepository.save(createPlaylist("Test Playlist", true, false, user));

        // 기본 플레이리스트 트랙 데이터 생성
        playlistTracks = List.of(
                playlistTrackRepository.save(createPlaylistTrack(playlist, tracks.get(0))),
                playlistTrackRepository.save(createPlaylistTrack(playlist, tracks.get(1))),
                playlistTrackRepository.save(createPlaylistTrack(playlist, tracks.get(2))));
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
    @DisplayName("GET /playlists/{playlistId} - Integration Success")
    @Transactional
    void getPlaylistWithTracksSuccess() throws Exception {
        /* Given */

        // 요청할 플레이리스트 ID
        Long playlistId = playlist.getId();

        /* When & Then */
        mockMvc.perform(get("/playlists/{playlistId}", playlistId).param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.playlistId").value(1))
                .andExpect(jsonPath("$.playlistName").value("Test Playlist"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.isCurated").value(false))
                .andExpect(jsonPath("$.ownerName").value("testDisplayName"))
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
