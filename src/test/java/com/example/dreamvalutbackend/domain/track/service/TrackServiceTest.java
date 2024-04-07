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
import com.example.dreamvalutbackend.domain.track.domain.StreamingHistory;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.StreamingHistoryRepository;
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
import static org.mockito.Mockito.verify;
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
    private StreamingHistoryRepository streamingHistoryRepository;

    @Mock
    private TagService tagService;

    @Mock
    private ImageUploader imageUploader;
    @Mock
    private AudioUploader audioUploader;
    @Mock
    private AudioDataParser audioDataParser;

    // TrackService 객체를 생성하면서 위에서 Mock으로 만든 객체들을 주입
    @InjectMocks
    private TrackService trackService;

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
                .title("title")
                .prompt("prompt")
                .hasLyrics(true)
                .tags(new String[] { "tag1", "tag2" })
                .genreId(1L)
                .build();

        // Mock MultipartFile 객체 생성 (track_image, track_audio)
        MockMultipartFile trackImage = new MockMultipartFile("track_image", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
                "image".getBytes());
        MockMultipartFile trackAudio = new MockMultipartFile("track_audio", "test.mp3", "audio/mpeg",
                "audio".getBytes());

        // Mock User, Genre, Track 객체 생성
        User user = createMockUser(1L, "userName", "displayName", "userEmail", "profileImage", UserRole.USER, "socialId");
        Genre genre = createMockGenre(1L, "genreName", "genreImage");
        Track track = createMockTrack(1L, "title", 100, true, "trackUrl", "trackImage", "thumbnailImage", user, genre);
        TrackDetail trackDetail = createMockTrackDetail(1L, "prompt", track);

        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        given(genreRepository.findById(any(Long.class))).willReturn(Optional.of(genre));
        given(imageUploader.uploadTrackImage(any(MockMultipartFile.class), any(String.class))).willReturn("trackImage");
        given(imageUploader.uploadThumbnailImage(any(MockMultipartFile.class), any(String.class)))
                .willReturn("thumbnailImage");
        given(audioUploader.uploadTrackAudio(any(MockMultipartFile.class), any(String.class))).willReturn("trackUrl");
        given(trackRepository.save(any(Track.class))).willReturn(track);
        given(trackDetailRepository.save(any(TrackDetail.class))).willReturn(trackDetail);

        /* When */
        TrackUploadResponseDto trackUploadResponseDto = trackService.uploadTrack(trackUploadRequestDto, trackImage,
                trackAudio);

        /* Then */
        assertThat(trackUploadResponseDto).isNotNull();
        assertThat(trackUploadResponseDto.getTrackId()).isEqualTo(track.getId());
        assertThat(trackUploadResponseDto.getTitle()).isEqualTo(trackUploadRequestDto.getTitle());
        assertThat(trackUploadResponseDto.getDuration()).isEqualTo(track.getDuration());
        assertThat(trackUploadResponseDto.getHasLyrics()).isEqualTo(trackUploadRequestDto.getHasLyrics());
        assertThat(trackUploadResponseDto.getTrackUrl()).isEqualTo(track.getTrackUrl());
        assertThat(trackUploadResponseDto.getTrackImage()).isEqualTo(track.getTrackImage());
        assertThat(trackUploadResponseDto.getThumbnailImage()).isEqualTo(track.getThumbnailImage());
    }

    @Test
    @DisplayName("GET /tracks/{track_id} - Unit Success")
    void getTrackSuccess() {
        /* Given */

        // 요청할 Track ID
        Long trackId = 1L;

        // Mock User, Genre, Track 객체 생성
        User user = createMockUser(1L, "userName", "displayName", "userEmail", "profileImage", UserRole.USER, "socialId");
        Genre genre = createMockGenre(1L, "genreName", "genreImage");
        Track track = createMockTrack(1L, "title", 100, true, "trackUrl", "trackImage", "thumbnailImage", user, genre);
        TrackDetail trackDetail = createMockTrackDetail(1L, "prompt", track);

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

    @Test
    @DisplayName("POST /tracks/{track_id}/stream_events - Unit Success")
    void recordStreamEventSuccess() {
        /* Given */

        // 요청할 Track ID
        Long trackId = 1L;

        // Mock User, Track 객체 생성
        User user = createMockUser(1L, "userName", "displayName", "userEmail", "profileImage", UserRole.USER, "socialId");
        Genre genre = createMockGenre(1L, "genreName", "genreImage");
        Track track = createMockTrack(1L, "title", 100, true, "trackUrl", "trackImage", "thumbnailImage", user, genre);

        // Mock Repository의 findById() 메소드가 리턴할 Optional 객체 생성
        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        given(trackRepository.findById(any(Long.class))).willReturn(Optional.of(track));

        /* When */
        trackService.recordStreamEvent(trackId);

        /* Then */
        verify(streamingHistoryRepository).save(any(StreamingHistory.class));
    }

    private User createMockUser(Long userId, String userName, String displayName, String userEmail, String profileImage,
            UserRole role, String socialId) {
        try {
            Constructor<User> constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            User user = constructor.newInstance();

            setField(user, "userId", userId);
            setField(user, "userName", userName);
            setField(user, "displayName", displayName);
            setField(user, "userEmail", userEmail);
            setField(user, "profileImage", profileImage);
            setField(user, "role", role);
            setField(user, "socialId", socialId);

            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock User", e);
        }
    }

    private Genre createMockGenre(Long id, String genreName, String genreImage) {
        try {
            Constructor<Genre> constructor = Genre.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Genre genre = constructor.newInstance();

            setField(genre, "id", id);
            setField(genre, "genreName", genreName);
            setField(genre, "genreImage", genreImage);

            return genre;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock Genre", e);
        }
    }

    private Track createMockTrack(Long id, String title, int duration, boolean hasLyrics,
            String trackUrl, String trackImage, String thumbnailImage, User user, Genre genre) {
        try {
            Constructor<Track> constructor = Track.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Track track = constructor.newInstance();

            setField(track, "id", id);
            setField(track, "title", title);
            setField(track, "duration", duration);
            setField(track, "hasLyrics", hasLyrics);
            setField(track, "trackUrl", trackUrl);
            setField(track, "trackImage", trackImage);
            setField(track, "thumbnailImage", thumbnailImage);
            setField(track, "user", user);
            setField(track, "genre", genre);

            return track;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock Track", e);
        }
    }

    private TrackDetail createMockTrackDetail(Long id, String prompt, Track track) {
        try {
            Constructor<TrackDetail> constructor = TrackDetail.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            TrackDetail trackDetail = constructor.newInstance();

            setField(trackDetail, "id", id);
            setField(trackDetail, "prompt", prompt);
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
