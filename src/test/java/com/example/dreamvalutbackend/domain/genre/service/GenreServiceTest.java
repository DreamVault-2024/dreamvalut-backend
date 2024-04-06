package com.example.dreamvalutbackend.domain.genre.service;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;

public class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private TrackDetailRepository trackDetailRepository;

    @InjectMocks
    private GenreService genreService;

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
    @DisplayName("GET /genres/list - Unit Success")
    public void listAllGenresSuccess() {
        /* Given */

        // 장르 목록을 반환하도록 설정
        given(genreRepository.findAll()).willReturn(Arrays.asList(
                Genre.builder().genreName("Pop").genreImage("pop_image_url").build(),
                Genre.builder().genreName("RnB").genreImage("rnb_image_url").build(),
                Genre.builder().genreName("Jazz").genreImage("jazz_image_url").build()));

        /* When */
        List<GenreResponseDto> genres = genreService.listAllGenres();

        /* Then */
        assertThat(genres.size()).isEqualTo(3);
        assertThat(genres.get(0).getGenreName()).isEqualTo("Pop");
        assertThat(genres.get(0).getGenreImage()).isEqualTo("pop_image_url");
        assertThat(genres.get(1).getGenreName()).isEqualTo("RnB");
        assertThat(genres.get(1).getGenreImage()).isEqualTo("rnb_image_url");
        assertThat(genres.get(2).getGenreName()).isEqualTo("Jazz");
        assertThat(genres.get(2).getGenreImage()).isEqualTo("jazz_image_url");
    }

    @Test
    @DisplayName("GET /genres/{genreId}/tracks - Unit Success")
    public void getGenreWithTracksSuccess() {
        /* Given */

        // 요청할 Genre ID
        Long genreId = 1L;

        // Mock User, Track 객체 생성
        User user = createMockUser();
        Genre genre = createMockGenre();
        Track track = createMockTrack(user, genre);
        TrackDetail trackDetail = createMockTrackDetail(track);
        Page<Track> trackPage = new PageImpl<>(List.of(track));

        given(genreRepository.findById(genreId)).willReturn(Optional.of(genre));
        given(trackRepository.findAllByGenreId(genreId, PageRequest.of(0, 30))).willReturn(trackPage);
        given(trackDetailRepository.findById(track.getId())).willReturn(Optional.of(trackDetail));

        /* When */
        GenreWithTracksResponseDto genreWithTracks = genreService.getGenreWithTracks(genreId, PageRequest.of(0, 30));

        /* Then */
        assertThat(genreWithTracks.getGenreId()).isEqualTo(GENRE_ID);
        assertThat(genreWithTracks.getGenreName()).isEqualTo(GENRE_NAME);
        assertThat(genreWithTracks.getGenreImage()).isEqualTo(GENRE_IMAGE);
        assertThat(genreWithTracks.getTracks().getContent().size()).isEqualTo(1);
        assertThat(genreWithTracks.getTracks().getContent().get(0).getTrackId()).isEqualTo(TRACK_ID);
        assertThat(genreWithTracks.getTracks().getContent().get(0).getTitle()).isEqualTo(TRACK_TITLE);
        assertThat(genreWithTracks.getTracks().getContent().get(0).getDuration()).isEqualTo(DURATION);
        assertThat(genreWithTracks.getTracks().getContent().get(0).getHasLyrics()).isEqualTo(HAS_LYRICS);
        assertThat(genreWithTracks.getTracks().getContent().get(0).getTrackUrl()).isEqualTo(TRACK_URL);
        assertThat(genreWithTracks.getTracks().getContent().get(0).getTrackImage()).isEqualTo(TRACK_IMAGE);
        assertThat(genreWithTracks.getTracks().getContent().get(0).getThumbnailImage()).isEqualTo(THUMBNAIL_IMAGE);
        assertThat(genreWithTracks.getTracks().getContent().get(0).getPrompt()).isEqualTo(PROMPT);
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
