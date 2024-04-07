package com.example.dreamvalutbackend.domain.track.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.tag.domain.TrackTag;
import com.example.dreamvalutbackend.domain.tag.repository.TagRepository;
import com.example.dreamvalutbackend.domain.tag.repository.TrackTagRepository;
import com.example.dreamvalutbackend.domain.track.controller.request.TrackUploadRequestDto;
import com.example.dreamvalutbackend.domain.track.domain.StreamingHistory;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.StreamingHistoryRepository;
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
public class TrackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrackRepository trackRepository;
    @Autowired
    private TrackDetailRepository trackDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TrackTagRepository trackTagRepository;
    @Autowired
    private StreamingHistoryRepository streamingHistoryRepository;

    private User user;
    private Genre genre;

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

        genre = Genre.builder()
                .genreName("testGenreName")
                .genreImage("testGenreImage")
                .build();
        genreRepository.save(genre);
    }

    @AfterEach
    void tearDown() {
        trackTagRepository.deleteAll();
        tagRepository.deleteAll();
        trackDetailRepository.deleteAll();
        trackRepository.deleteAll();
        genreRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /tracks Integration Success")
    @Transactional
    void uploadTrackSuccess() throws Exception {
        /* Given */

        // TrackUploadRequestDto 객체 생성
        TrackUploadRequestDto trackUploadRequestDto = TrackUploadRequestDto.builder()
                .title("Sample Track")
                .prompt("Sample Prompt")
                .hasLyrics(true)
                .tags(new String[] { "tag1", "tag2" })
                .genreId(1L)
                .build();

        // TrackUploadRequestDto 객체를 JSON 문자열로 변환
        String trackInfoJson = objectMapper.writeValueAsString(trackUploadRequestDto);

        // MockMultipartFile 객체 생성 (track_info, track_image, track_audio)
        MockMultipartFile trackInfo = new MockMultipartFile("track_info", "track_info.json", "application/json",
                trackInfoJson.getBytes());
        MockMultipartFile trackImage = new MockMultipartFile("track_image", "track_image.jpg", "image/jpeg",
                "image".getBytes());
        MockMultipartFile trackAudio = new MockMultipartFile("track_audio", "track_audio.mp3", "audio/mpeg",
                "audio".getBytes());

        /* When & Then */

        /*
         * 해당 부분에서 아래와 같은 오류 발생 할 수 있음 - TrackService에서 User를 임시로 1L로만 가져옴
         * 단일 테스트에서는 문제가 없지만, 여러 테스트를 동시에 실행할 경우 User를 찾을 수 없다는 오류 발생
         * 
         * jakarta.servlet.ServletException: Request processing failed:
         * jakarta.persistence.EntityNotFoundException: User not found with id: 1
         */
        mockMvc.perform(multipart("/tracks")
                .file(trackInfo)
                .file(trackImage)
                .file(trackAudio)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated());

        // trackRepository에서 저장된 Track 엔티티 검증
        Track savedTrack = trackRepository.findById(1L).orElseThrow();
        assertThat(savedTrack.getTitle()).isEqualTo(trackUploadRequestDto.getTitle());
        assertThat(savedTrack.getDuration()).isGreaterThanOrEqualTo(0);
        assertThat(savedTrack.getHasLyrics()).isEqualTo(trackUploadRequestDto.getHasLyrics());
        assertThat(savedTrack.getTrackImage()).isNotNull();
        assertThat(savedTrack.getThumbnailImage()).isNotNull();
        assertThat(savedTrack.getUser().getUserId()).isEqualTo(user.getUserId());
        assertThat(savedTrack.getGenre().getId()).isEqualTo(genre.getId());

        // trackDetailRepository에서 저장된 TrackDetail 엔티티 검증
        TrackDetail savedTrackDetail = trackDetailRepository.findById(1L).orElseThrow();
        assertThat(savedTrackDetail.getPrompt()).isEqualTo(trackUploadRequestDto.getPrompt());

        // trackTagRepository에서 저장된 TrackTag 엔티티들 검증
        List<TrackTag> trackTagsForTag1 = trackTagRepository.findByTagName("tag1");
        List<TrackTag> trackTagsForTag2 = trackTagRepository.findByTagName("tag2");

        // Verify that the tags are associated with the correct Track
        boolean isTag1AssociatedWithTrack = trackTagsForTag1.stream()
                .anyMatch(trackTag -> trackTag.getTrack().getId().equals(savedTrack.getId()));
        boolean isTag2AssociatedWithTrack = trackTagsForTag2.stream()
                .anyMatch(trackTag -> trackTag.getTrack().getId().equals(savedTrack.getId()));

        assertThat(isTag1AssociatedWithTrack).isTrue();
        assertThat(isTag2AssociatedWithTrack).isTrue();
    }

    @Test
    @DisplayName("GET /tracks/{track_id} Integration Success")
    @Transactional
    void getTrackSuccess() throws Exception {
        /* Given */

        // Track 엔티티 생성
        Track track = Track.builder()
                .title("Sample Track")
                .duration(120)
                .hasLyrics(true)
                .trackUrl("testTrackUrl")
                .trackImage("testTrackImage")
                .thumbnailImage("testThumbnailImage")
                .user(user)
                .genre(genre)
                .build();
        Track savedTrack = trackRepository.save(track);

        // TrackDetail 엔티티 생성
        TrackDetail trackDetail = TrackDetail.builder()
                .prompt("Sample Prompt")
                .track(savedTrack)
                .build();
        trackDetailRepository.save(trackDetail);

        /* When & Then */
        mockMvc.perform(get("/tracks/{track_id}", savedTrack.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackId").value(savedTrack.getId()))
                .andExpect(jsonPath("$.title").value("Sample Track"))
                .andExpect(jsonPath("$.uploaderName").value("testDisplayName"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.hasLyrics").value(true))
                .andExpect(jsonPath("$.trackUrl").isString())
                .andExpect(jsonPath("$.trackImage").value("testTrackImage"))
                .andExpect(jsonPath("$.thumbnailImage").value("testThumbnailImage"))
                .andExpect(jsonPath("$.prompt").value("Sample Prompt"));
    }

    @Test
    @DisplayName("POST /tracks/{track_id}/stream_events Integration Success")
    @Transactional
    void recordStreamEventSuccess() throws Exception {
        /* Given */

        // Track 엔티티 생성
        Track track = Track.builder()
                .title("Sample Track")
                .duration(120)
                .hasLyrics(true)
                .trackUrl("testTrackUrl")
                .trackImage("testTrackImage")
                .thumbnailImage("testThumbnailImage")
                .user(user)
                .genre(genre)
                .build();
        Track savedTrack = trackRepository.save(track);

        // TrackDetail 엔티티 생성
        TrackDetail trackDetail = TrackDetail.builder()
                .prompt("Sample Prompt")
                .track(savedTrack)
                .build();
        trackDetailRepository.save(trackDetail);

        /* When & Then */
        mockMvc.perform(post("/tracks/{track_id}/stream_events", savedTrack.getId()))
                .andExpect(status().isOk());

        // streamingHistoryRepository에서 저장된 StreamingHistory 엔티티 검증
        List<StreamingHistory> streamingHistories = streamingHistoryRepository.findAll();
        assertThat(streamingHistories).hasSize(1);
        assertThat(streamingHistories.get(0).getUser().getUserId()).isEqualTo(user.getUserId());
        assertThat(streamingHistories.get(0).getTrack().getId()).isEqualTo(savedTrack.getId());
    }
}
