package com.example.dreamvalutbackend.domain.track.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.tag.repository.TagRepository;
import com.example.dreamvalutbackend.domain.tag.repository.TrackTagRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TrackControllerTest {

    // HTTP 요청 처리를 위한 MockMvc 객체
    @Autowired
    private MockMvc mockMvc;

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

    @Value("${aws.s3.bucket}")
    private String s3BucketName;
    @Value("${aws.s3.default-image}")
    private String defaultImageUrl;

    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .userName("testUserName")
                .displayName("testDisplayName")
                .email("testEmail")
                .profileImage("testProfileImage")
                .build();
        userRepository.save(user);

        Genre genre = Genre.builder()
                .genreName("testGenreName")
                .genreImage("testGenreImage")
                .build();
        genreRepository.save(genre);
    }

    @AfterEach
    public void tearDown() {
        trackTagRepository.deleteAll();
        tagRepository.deleteAll();
        trackDetailRepository.deleteAll();
        trackRepository.deleteAll();
        userRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("TrackController - 트랙 업로드 통합 테스트")
    public void uploadTrackIntegrationTest() throws Exception {
        /* Given */

        // HTTP Post 요청 시, track_info 파트에 전달할 JSON 데이터
        String trackInfoJson = "{\n" +
                "  \"title\": \"TestTitle\",\n" +
                "  \"prompt\": \"TestPrompt\",\n" +
                "  \"hasLyrics\": true,\n" +
                "  \"tags\": [\"TestTag1\", \"TestTag2\"],\n" +
                "  \"genreId\": 1\n" +
                "}";

        // Mock MultipartFile 객체 생성 (track_info, track_image, track_audio)
        MockMultipartFile trackInfo = new MockMultipartFile("track_info", "test.json", "application/json",
                trackInfoJson.getBytes());
        MockMultipartFile trackImage = new MockMultipartFile("track_image", "test.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "image".getBytes());
        MockMultipartFile trackAudio = new MockMultipartFile("track_audio", "test.mp3", "audio/mpeg",
                "audio".getBytes());

        /* When & Then */
        mockMvc.perform(multipart("/api/v1/tracks")
                .file(trackInfo)
                .file(trackImage)
                .file(trackAudio)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/tracks/1"))
                .andExpect(jsonPath("$.trackId").value(1L))
                .andExpect(jsonPath("$.title").value("TestTitle"))
                .andExpect(jsonPath("$.hasLyrics").value(true))
                .andExpect(jsonPath("$.trackUrl").value(
                        matchesPattern(generateS3Pattern(s3BucketName, "mp3", "TestTitle"))))
                .andExpect(jsonPath("$.trackImage").value(
                        matchesPattern(generateS3Pattern(s3BucketName, "jpeg", "TestTitle"))))
                .andExpect(jsonPath("$.thumbnailImage").value(defaultImageUrl));
    }

    // S3 URL 패턴 생성 메서드
    private String generateS3Pattern(String s3BucketName, String fileType, String title) {
        return String.format("https://%s\\.s3\\.ap-northeast-2\\.amazonaws\\.com/%s/[\\w-]+-%s\\.%s",
                s3BucketName, fileType.equals("mp3") ? "audio" : "image", title, fileType);
    }
}
