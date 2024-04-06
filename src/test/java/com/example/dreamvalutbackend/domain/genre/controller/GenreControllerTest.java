package com.example.dreamvalutbackend.domain.genre.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private TrackRepository trackRepository;
    @Autowired
    private TrackDetailRepository trackDetailRepository;

    private User user;
    private Genre pop;
    private Genre rnb;
    private Genre jazz;
    private Track track;
    private TrackDetail trackDetail;

    @BeforeEach
    public void setUp() {
        // 기본 장르 데이터 생성
        pop = genreRepository.save(Genre.builder().genreName("Pop").genreImage("pop_image_url").build());
        rnb = genreRepository.save(Genre.builder().genreName("RnB").genreImage("rnb_image_url").build());
        jazz = genreRepository.save(Genre.builder().genreName("Jazz").genreImage("jazz_image_url").build());

        // 기본 유저 데이터 생성
        user = User.builder()
                .userName("testUserName")
                .displayName("testDisplayName")
                .userEmail("testEmail")
                .profileImage("testProfileImage")
                .role(UserRole.USER)
                .socialId("testSocialId")
                .build();
        userRepository.save(user);

        // 기본 트랙 데이터 생성
        track = Track.builder()
                .title("track1")
                .duration(100)
                .hasLyrics(true)
                .trackUrl("track1_url")
                .trackImage("track1_image")
                .thumbnailImage("track1_thumbnail")
                .genre(pop)
                .user(user)
                .build();
        trackRepository.save(track);

        trackDetail = TrackDetail.builder()
                .track(track)
                .prompt("track1_prompt")
                .build();
        trackDetailRepository.save(trackDetail);
    }

    @AfterEach
    public void tearDown() {
        trackDetailRepository.deleteAll();
        trackRepository.deleteAll();
        userRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /genres/list - Integration Success")
    @Transactional
    void listAllGenresSuccess() throws Exception {
        mockMvc.perform(get("/genres/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genreName").value("Pop"))
                .andExpect(jsonPath("$[0].genreImage").value("pop_image_url"))
                .andExpect(jsonPath("$[1].genreName").value("RnB"))
                .andExpect(jsonPath("$[1].genreImage").value("rnb_image_url"))
                .andExpect(jsonPath("$[2].genreName").value("Jazz"))
                .andExpect(jsonPath("$[2].genreImage").value("jazz_image_url"));
    }

    @Test
    @DisplayName("GET /genres/{genre_id}/tracks - Integration Success")
    @Transactional
    void getGenreWithTracksSuccess() throws Exception {
        mockMvc.perform(get("/genres/{genre_id}/tracks", pop.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genreId").value(pop.getId()))
                .andExpect(jsonPath("$.genreName").value(pop.getGenreName()))
                .andExpect(jsonPath("$.genreImage").value(pop.getGenreImage()))
                .andExpect(jsonPath("$.tracks.content[0].trackId").value(track.getId()))
                .andExpect(jsonPath("$.tracks.content[0].title").value(track.getTitle()))
                .andExpect(jsonPath("$.tracks.content[0].uploaderName").value(user.getDisplayName()))
                .andExpect(jsonPath("$.tracks.content[0].duration").value(track.getDuration()))
                .andExpect(jsonPath("$.tracks.content[0].hasLyrics").value(track.getHasLyrics()))
                .andExpect(jsonPath("$.tracks.content[0].trackUrl").value(track.getTrackUrl()))
                .andExpect(jsonPath("$.tracks.content[0].trackImage").value(track.getTrackImage()))
                .andExpect(jsonPath("$.tracks.content[0].thumbnailImage").value(track.getThumbnailImage()))
                .andExpect(jsonPath("$.tracks.content[0].prompt").value(trackDetail.getPrompt()))
                .andExpect(jsonPath("$.tracks.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.tracks.pageable.pageSize").value(30))
                .andExpect(jsonPath("$.tracks.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.tracks.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.tracks.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.tracks.pageable.offset").value(0))
                .andExpect(jsonPath("$.tracks.pageable.paged").value(true))
                .andExpect(jsonPath("$.tracks.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.tracks.last").value(true))
                .andExpect(jsonPath("$.tracks.totalElements").value(1))
                .andExpect(jsonPath("$.tracks.totalPages").value(1))
                .andExpect(jsonPath("$.tracks.number").value(0))
                .andExpect(jsonPath("$.tracks.sort.empty").value(false))
                .andExpect(jsonPath("$.tracks.sort.unsorted").value(false))
                .andExpect(jsonPath("$.tracks.sort.sorted").value(true))
                .andExpect(jsonPath("$.tracks.size").value(30))
                .andExpect(jsonPath("$.tracks.first").value(true))
                .andExpect(jsonPath("$.tracks.numberOfElements").value(1))
                .andExpect(jsonPath("$.tracks.empty").value(false));
    }
}
