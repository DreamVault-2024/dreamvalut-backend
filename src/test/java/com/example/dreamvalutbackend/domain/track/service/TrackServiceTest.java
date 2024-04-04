package com.example.dreamvalutbackend.domain.track.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.tag.service.TagService;
import com.example.dreamvalutbackend.domain.track.controller.request.TrackUploadRequestDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackUploadResponseDto;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;
import com.example.dreamvalutbackend.global.utils.audio.AudioDataParser;
import com.example.dreamvalutbackend.global.utils.uploader.AudioUploader;
import com.example.dreamvalutbackend.global.utils.uploader.ImageUploader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Optional;

public class TrackServiceTest {

    // 아래의 객체들을 Mock으로 만들어 주입
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private TrackDetailRepository trackDetailRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private ImageUploader imageUploader;
    @Mock
    private AudioUploader audioUploader;
    @Mock
    private AudioDataParser audioDataParser;
    @Mock
    private TagService tagService;

    // TrackService 객체를 생성하면서 위에서 Mock으로 만든 객체들을 주입
    @InjectMocks
    private TrackService trackService;

    private static final Long TRACK_ID = 1L;
    private static final String TRACK_TITLE = "Test Title";
    private static final Integer DURATION = 120;
    private static final Boolean HAS_LYRICS = true;
    private static final String TRACK_URL = "https://example-bucket.s3.amazonaws.com/audio/testTrackUrl.wav";
    private static final String TRACK_IMAGE = "https://example-bucket.s3.amazonaws.com/image/testTrackImage.jpeg";
    private static final String THUMBNAIL_IMAGE = "https://example-bucket.s3.amazonaws.com/image/testThumbnailImage.jpeg";
    private static final String PROMPT = "Test Prompt";
    private static final String[] TAGS = { "Test Tag1", "Test Tag2" };
    private static final Long GENRE_ID = 1L;
    private static final String GENRE_NAME = "Test Genre";
    private static final String GENRE_IMAGE = "https://example.com/genre/test.jpg";
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "Test User";
    private static final String DISPLAY_NAME = "Test User";
    private static final String USER_EMAIL = "testuser@example.com";
    private static final String PROFILE_IMAGE = "https://example.com/profile/testuser.jpg";
    private static final UserRole USER_ROLE = UserRole.USER;
    private static final String USER_SOCIAL_ID = "testSocialId";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("POST /tracks - Unit Success")
    void uploadTrackSuccess() throws IOException, NoSuchFieldException, IllegalAccessException {
        /* Given */

        // 테스트용 TrackUploadRequestDto 객체 생성
        TrackUploadRequestDto trackUploadRequestDto = TrackUploadRequestDto.builder()
                .title(TRACK_TITLE)
                .prompt(PROMPT)
                .hasLyrics(HAS_LYRICS)
                .tags(TAGS)
                .genreId(GENRE_ID)
                .build();

        // Mock MultipartFile 객체 생성 (track_image, track_audio)
        MockMultipartFile trackImage = new MockMultipartFile("track_image", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
                "image".getBytes());
        MockMultipartFile trackAudio = new MockMultipartFile("track_audio", "test.mp3", "audio/mpeg",
                "audio".getBytes());

        // Mock User, Genre, Track 객체 생성
        User user = createMockUser();
        Genre genre = createMockGenre();
        Track track = createMockTrack(user, genre);
        TrackDetail trackDetail = createMockTrackDetail(track);

        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        given(genreRepository.findById(any(Long.class))).willReturn(Optional.of(genre));
        given(imageUploader.uploadTrackImage(any(MockMultipartFile.class), any(String.class))).willReturn(TRACK_IMAGE);
        given(imageUploader.uploadThumbnailImage(any(MockMultipartFile.class), any(String.class)))
                .willReturn(THUMBNAIL_IMAGE);
        given(audioUploader.uploadTrackAudio(any(MockMultipartFile.class), any(String.class))).willReturn(TRACK_URL);
        given(trackRepository.save(any(Track.class))).willReturn(track);
        given(trackDetailRepository.save(any(TrackDetail.class))).willReturn(trackDetail);

        /* When */
        TrackUploadResponseDto trackUploadResponseDto = trackService.uploadTrack(trackUploadRequestDto, trackImage,
                trackAudio);

        /* Then */
        assertThat(trackUploadResponseDto).isNotNull();
        assertThat(trackUploadResponseDto.getTrackId()).isEqualTo(track.getId());
        assertThat(trackUploadResponseDto.getTitle()).isEqualTo(trackUploadRequestDto.getTitle());
        assertThat(trackUploadResponseDto.getDuration()).isEqualTo(DURATION);
        assertThat(trackUploadResponseDto.getHasLyrics()).isEqualTo(trackUploadRequestDto.getHasLyrics());
        assertThat(trackUploadResponseDto.getTrackUrl()).isEqualTo(TRACK_URL);
        assertThat(trackUploadResponseDto.getTrackImage()).isEqualTo(TRACK_IMAGE);
        assertThat(trackUploadResponseDto.getThumbnailImage()).isEqualTo(THUMBNAIL_IMAGE);
    }

    @Test
    @DisplayName("GET /tracks/{track_id} - Unit Success")
    void getTrackSuccess() {
        /* Given */

        // 요청할 Track ID
        Long trackId = TRACK_ID;

        // Mock User, Genre, Track 객체 생성
        User user = createMockUser();
        Genre genre = createMockGenre();
        Track track = createMockTrack(user, genre);
        TrackDetail trackDetail = createMockTrackDetail(track);

        // Mock Repository의 findById() 메소드가 리턴할 Optional 객체 생성
        given(trackRepository.findById(any(Long.class))).willReturn(Optional.of(track));
        given(trackDetailRepository.findById(any(Long.class))).willReturn(Optional.of(trackDetail));

        /* When */
        TrackResponseDto trackResponseDto = trackService.getTrack(trackId);

        /* Then */
        assertThat(trackResponseDto).isNotNull();
        assertThat(trackResponseDto.getTrackId()).isEqualTo(track.getId());
        assertThat(trackResponseDto.getTitle()).isEqualTo(track.getTitle());
        assertThat(trackResponseDto.getUploaderName()).isEqualTo(user.getDisplayName());
        assertThat(trackResponseDto.getDuration()).isEqualTo(track.getDuration());
        assertThat(trackResponseDto.getHasLyrics()).isEqualTo(track.getHasLyrics());
        assertThat(trackResponseDto.getTrackUrl()).isEqualTo(track.getTrackUrl());
        assertThat(trackResponseDto.getTrackImage()).isEqualTo(track.getTrackImage());
        assertThat(trackResponseDto.getThumbnailImage()).isEqualTo(track.getThumbnailImage());
        assertThat(trackResponseDto.getPrompt()).isEqualTo(trackDetail.getPrompt());
    }

    private User createMockUser() {
        try {
            Constructor<User> constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            User user = constructor.newInstance();

            setField(user, "userId", USER_ID);
            setField(user, "userName", USER_NAME);
            setField(user, "displayName", DISPLAY_NAME);
            setField(user, "userEmail", USER_EMAIL);
            setField(user, "profileImage", PROFILE_IMAGE);
            setField(user, "role", USER_ROLE);
            setField(user, "socialId", USER_SOCIAL_ID);

            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock User", e);
        }
    }

    private Genre createMockGenre() {
        try {
            Constructor<Genre> constructor = Genre.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Genre genre = constructor.newInstance();

            setField(genre, "id", GENRE_ID);
            setField(genre, "genreName", GENRE_NAME);
            setField(genre, "genreImage", GENRE_IMAGE);

            return genre;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock Genre", e);
        }
    }

    private Track createMockTrack(User user, Genre genre) {
        try {
            Constructor<Track> constructor = Track.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Track track = constructor.newInstance();

            setField(track, "id", TRACK_ID);
            setField(track, "title", TRACK_TITLE);
            setField(track, "duration", DURATION);
            setField(track, "hasLyrics", HAS_LYRICS);
            setField(track, "trackUrl", TRACK_URL);
            setField(track, "trackImage", TRACK_IMAGE);
            setField(track, "thumbnailImage", THUMBNAIL_IMAGE);
            setField(track, "user", user);
            setField(track, "genre", genre);

            return track;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock Track", e);
        }
    }

    private TrackDetail createMockTrackDetail(Track track) {
        try {
            Constructor<TrackDetail> constructor = TrackDetail.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            TrackDetail trackDetail = constructor.newInstance();

            setField(trackDetail, "id", TRACK_ID);
            setField(trackDetail, "prompt", PROMPT);
            setField(trackDetail, "track", track);

            return trackDetail;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock TrackDetail", e);
        }
    }

    private void setField(Object target, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
