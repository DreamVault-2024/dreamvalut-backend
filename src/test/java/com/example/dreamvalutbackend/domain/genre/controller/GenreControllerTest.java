package com.example.dreamvalutbackend.domain.genre.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private List<Track> popTracks;
    private List<Track> rnbTracks;
    private List<Track> jazzTracks;
    private List<TrackDetail> popTrackDetails;
    private List<TrackDetail> rnbTrackDetails;
    private List<TrackDetail> jazzTrackDetails;

    @BeforeEach
    public void setUp() {
        // 기본 유저 데이터 생성
        user = userRepository.save(createUser("testUserName", "testDisplayName", "testEmail", "testProfileImage",
                UserRole.USER, "testSocialId"));

        // 3개 장르 데이터 생성
        pop = genreRepository.save(createGenre("Pop", "pop_image_url"));
        rnb = genreRepository.save(createGenre("RnB", "rnb_image_url"));
        jazz = genreRepository.save(createGenre("Jazz", "jazz_image_url"));

        // 3개 장르별 트랙 데이터 생성
        popTracks = List.of(
                trackRepository.save(
                        createTrack("track1", 100, true, "track1_url", "track1_image", "track1_thumbnail", user, pop)),
                trackRepository.save(
                        createTrack("track2", 120, false, "track2_url", "track2_image", "track2_thumbnail", user, pop)),
                trackRepository.save(
                        createTrack("track3", 140, true, "track3_url", "track3_image", "track3_thumbnail", user, pop)));

        rnbTracks = List.of(
                trackRepository.save(
                        createTrack("track4", 100, true, "track4_url", "track4_image", "track4_thumbnail", user, rnb)),
                trackRepository.save(
                        createTrack("track5", 120, false, "track5_url", "track5_image", "track5_thumbnail", user, rnb)),
                trackRepository.save(
                        createTrack("track6", 140, true, "track6_url", "track6_image", "track6_thumbnail", user, rnb)));

        jazzTracks = List.of(
                trackRepository.save(
                        createTrack("track7", 100, true, "track7_url", "track7_image", "track7_thumbnail", user, jazz)),
                trackRepository.save(createTrack("track8", 120, false, "track8_url", "track8_image", "track8_thumbnail",
                        user, jazz)),
                trackRepository.save(createTrack("track9", 140, true, "track9_url", "track9_image", "track9_thumbnail",
                        user, jazz)));

        // 3개 장르별 트랙 상세 데이터 생성
        popTrackDetails = List.of(
                trackDetailRepository.save(createTrackDetail(popTracks.get(0), "pop_prompt1")),
                trackDetailRepository.save(createTrackDetail(popTracks.get(1), "pop_prompt2")),
                trackDetailRepository.save(createTrackDetail(popTracks.get(2), "pop_prompt3")));
        rnbTrackDetails = List.of(
                trackDetailRepository.save(createTrackDetail(rnbTracks.get(0), "rnb_prompt1")),
                trackDetailRepository.save(createTrackDetail(rnbTracks.get(1), "rnb_prompt2")),
                trackDetailRepository.save(createTrackDetail(rnbTracks.get(2), "rnb_prompt3")));
        jazzTrackDetails = List.of(
                trackDetailRepository.save(createTrackDetail(jazzTracks.get(0), "jazz_prompt1")),
                trackDetailRepository.save(createTrackDetail(jazzTracks.get(1), "jazz_prompt2")),
                trackDetailRepository.save(createTrackDetail(jazzTracks.get(2), "jazz_prompt3")));
    }

    @AfterEach
    public void tearDown() {
        trackDetailRepository.deleteAll();
        trackRepository.deleteAll();
        userRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /genres - Integration Success")
    @Transactional
    void getGenresWithTracksOverviewSuccess() throws Exception {
        mockMvc.perform(get("/genres").param("page", "0").param("size", "3"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].genreId").value(pop.getId()))
                .andExpect(jsonPath("$.content[0].genreName").value(pop.getGenreName()))
                .andExpect(jsonPath("$.content[0].genreImage").value(pop.getGenreImage()))
                .andExpect(jsonPath("$.content[0].tracks.length()").value(3))
                .andExpect(jsonPath("$.content[0].tracks[0].trackId").value(popTracks.get(2).getId()))
                .andExpect(jsonPath("$.content[0].tracks[0].title").value(popTracks.get(2).getTitle()))
                .andExpect(jsonPath("$.content[0].tracks[0].uploaderName").value(user.getDisplayName()))
                .andExpect(jsonPath("$.content[0].tracks[0].thumbnailImage").value(popTracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.content[1].genreId").value(rnb.getId()))
                .andExpect(jsonPath("$.content[1].genreName").value(rnb.getGenreName()))
                .andExpect(jsonPath("$.content[1].genreImage").value(rnb.getGenreImage()))
                .andExpect(jsonPath("$.content[1].tracks.length()").value(3))
                .andExpect(jsonPath("$.content[1].tracks[0].trackId").value(rnbTracks.get(2).getId()))
                .andExpect(jsonPath("$.content[1].tracks[0].title").value(rnbTracks.get(2).getTitle()))
                .andExpect(jsonPath("$.content[1].tracks[0].uploaderName").value(user.getDisplayName()))
                .andExpect(jsonPath("$.content[1].tracks[0].thumbnailImage").value(rnbTracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.content[2].genreId").value(jazz.getId()))
                .andExpect(jsonPath("$.content[2].genreName").value(jazz.getGenreName()))
                .andExpect(jsonPath("$.content[2].genreImage").value(jazz.getGenreImage()))
                .andExpect(jsonPath("$.content[2].tracks.length()").value(3))
                .andExpect(jsonPath("$.content[2].tracks[0].trackId").value(jazzTracks.get(2).getId()))
                .andExpect(jsonPath("$.content[2].tracks[0].title").value(jazzTracks.get(2).getTitle()))
                .andExpect(jsonPath("$.content[2].tracks[0].uploaderName").value(user.getDisplayName()))
                .andExpect(jsonPath("$.content[2].tracks[0].thumbnailImage").value(jazzTracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(3))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.numberOfElements").value(3))
                .andExpect(jsonPath("$.empty").value(false));
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
                .andDo(print())
                .andExpect(jsonPath("$.genreId").value(pop.getId()))
                .andExpect(jsonPath("$.genreName").value(pop.getGenreName()))
                .andExpect(jsonPath("$.genreImage").value(pop.getGenreImage()))
                .andExpect(jsonPath("$.tracks.content.length()").value(3))
                .andExpect(jsonPath("$.tracks.content[0].trackId").value(popTracks.get(2).getId()))
                .andExpect(jsonPath("$.tracks.content[0].title").value(popTracks.get(2).getTitle()))
                .andExpect(jsonPath("$.tracks.content[0].uploaderName").value(user.getDisplayName()))
                .andExpect(jsonPath("$.tracks.content[0].duration").value(popTracks.get(2).getDuration()))
                .andExpect(jsonPath("$.tracks.content[0].hasLyrics").value(popTracks.get(2).getHasLyrics()))
                .andExpect(jsonPath("$.tracks.content[0].trackUrl").value(popTracks.get(2).getTrackUrl()))
                .andExpect(jsonPath("$.tracks.content[0].trackImage").value(popTracks.get(2).getTrackImage()))
                .andExpect(jsonPath("$.tracks.content[0].thumbnailImage").value(popTracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.tracks.content[0].prompt").value(popTrackDetails.get(2).getPrompt()))
                .andExpect(jsonPath("$.tracks.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.tracks.pageable.pageSize").value(30))
                .andExpect(jsonPath("$.tracks.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.tracks.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.tracks.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.tracks.pageable.offset").value(0))
                .andExpect(jsonPath("$.tracks.pageable.paged").value(true))
                .andExpect(jsonPath("$.tracks.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.tracks.last").value(true))
                .andExpect(jsonPath("$.tracks.totalPages").value(1))
                .andExpect(jsonPath("$.tracks.totalElements").value(3))
                .andExpect(jsonPath("$.tracks.size").value(30))
                .andExpect(jsonPath("$.tracks.sort.empty").value(false))
                .andExpect(jsonPath("$.tracks.sort.sorted").value(true))
                .andExpect(jsonPath("$.tracks.sort.unsorted").value(false))
                .andExpect(jsonPath("$.tracks.first").value(true))
                .andExpect(jsonPath("$.tracks.number").value(0))
                .andExpect(jsonPath("$.tracks.numberOfElements").value(3))
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
}
