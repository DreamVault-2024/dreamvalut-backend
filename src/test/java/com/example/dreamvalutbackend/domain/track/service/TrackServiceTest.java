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
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;
import com.example.dreamvalutbackend.global.utils.uploader.AudioUploader;
import com.example.dreamvalutbackend.global.utils.uploader.ImageUploader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    private TagService tagService;

    // TrackService 객체를 생성하면서 위에서 Mock으로 만든 객체들을 주입
    @InjectMocks
    private TrackService trackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TrackService - 트랙 업로드 테스트")
    void uploadTrackTest() throws IOException, NoSuchFieldException, IllegalAccessException {
        /* Given */

        // 테스트용 TrackUploadRequestDto 객체 생성
        TrackUploadRequestDto trackUploadRequestDto = TrackUploadRequestDto.builder()
                .title("TestTitle")
                .prompt("TestPrompt")
                .hasLyrics(true)
                .tags(new String[] { "TestTag1", "TestTag2" })
                .genreId(1L)
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

        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        given(genreRepository.findById(any(Long.class))).willReturn(Optional.of(genre));
        given(imageUploader.uploadTrackImage(any(MockMultipartFile.class), any(String.class)))
                .willReturn("https://example-bucket.s3.amazonaws.com/image/testTrackImage.jpeg");
        given(imageUploader.uploadThumbnailImage(any(MockMultipartFile.class), any(String.class)))
                .willReturn("https://example-bucket.s3.amazonaws.com/image/testThumbnailImage.jpeg");
        given(audioUploader.uploadTrackAudio(any(MockMultipartFile.class), any(String.class)))
                .willReturn("https://example-bucket.s3.amazonaws.com/audio/testTrackUrl.wav");
        given(trackRepository.save(any(Track.class))).willReturn(track);

        /* When */
        trackService.uploadTrack(trackUploadRequestDto, trackImage, trackAudio);

        /* Then */
        verify(trackRepository, times(1)).save(any(Track.class));
        verify(trackDetailRepository, times(1)).save(any(TrackDetail.class));
    }

    private User createMockUser() {
        try {
            Constructor<User> constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            User user = constructor.newInstance();

            setField(user, "id", 1L);
            setField(user, "userName", "TestUser");
            setField(user, "displayName", "Test User");
            setField(user, "email", "testuser@example.com");
            setField(user, "profileImage", "https://example.com/profile/testuser.jpg");

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

            setField(genre, "id", 1L);
            setField(genre, "genreName", "Test Genre");
            setField(genre, "genreImage", "https://example.com/genre/test.jpg");

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

            setField(track, "id", 1L);
            setField(track, "title", "Test Track");
            setField(track, "hasLyrics", true);
            setField(track, "trackUrl", "https://example.com/track/test.wav");
            setField(track, "trackImage", "https://example.com/image/test.jpg");
            setField(track, "thumbnailImage", "https://example.com/image/testThumbnail.jpg");
            setField(track, "user", user);
            setField(track, "genre", genre);

            return track;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock Track", e);
        }
    }

    private void setField(Object target, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
