package com.example.dreamvalutbackend.domain.genre.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;

public class GenreServiceUnitTest {

    @Mock
    private GenreRepository genreRepository;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private TrackDetailRepository trackDetailRepository;

    @InjectMocks
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("GET /genres - Unit Success")
    void getGenresWithTracksOverviewSuccess() {
        /* Given */

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 4, Sort.by("id").ascending());

        // Mock 장르 객체 생성
        Genre genre1 = createMockGenre(1L, "Genre 1", "genre1_image_url");
        Genre genre2 = createMockGenre(2L, "Genre 2", "genre2_image_url");

        // Mock 유저 객체 생성
        User user = createMockUser(1L, "testUser", "testDisplay", "test@example.com", "profileImageUrl", UserRole.USER,
                "socialId123");

        // 반환할 장르 페이지 객체 생성
        Page<Genre> genresPage = new PageImpl<>(List.of(genre1, genre2));

        List<Track> tracks = List.of(
                createMockTrack(1L, "Track 1", 300, true, "trackUrl1", "trackImage1", "thumbnailImage1", user, genre1),
                createMockTrack(2L, "Track 2", 300, true, "trackUrl2", "trackImage2", "thumbnailImage2", user, genre1));

        // Mock Repository 설정
        given(genreRepository.findAll(pageable)).willReturn(genresPage);

        // 임의로 genre2에 대한 트랙을 genre1의 트랙으로 설정
        given(trackRepository.findAllByGenreId(anyLong(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(tracks));

        /* When */
        Page<GenreWithTracksOverviewResponseDto> result = genreService.getGenresWithTracksOverview(pageable);

        /* Then */

        assertThat(result.getContent().size()).isEqualTo(2);

        assertThat(result.getContent().get(0).getGenreId()).isEqualTo(genre1.getId());
        assertThat(result.getContent().get(0).getGenreName()).isEqualTo(genre1.getGenreName());
        assertThat(result.getContent().get(0).getGenreImage()).isEqualTo(genre1.getGenreImage());
        assertThat(result.getContent().get(0).getTracks().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getTracks().get(0).getTrackId()).isEqualTo(tracks.get(0).getId());
        assertThat(result.getContent().get(0).getTracks().get(0).getTitle()).isEqualTo(tracks.get(0).getTitle());
        assertThat(result.getContent().get(0).getTracks().get(0).getUploaderName()).isEqualTo(user.getDisplayName());
        assertThat(result.getContent().get(0).getTracks().get(0).getThumbnailImage()).isEqualTo(tracks.get(0).getThumbnailImage());
        assertThat(result.getContent().get(0).getTracks().get(1).getTrackId()).isEqualTo(tracks.get(1).getId());
        assertThat(result.getContent().get(0).getTracks().get(1).getTitle()).isEqualTo(tracks.get(1).getTitle());
        assertThat(result.getContent().get(0).getTracks().get(1).getUploaderName()).isEqualTo(user.getDisplayName());
        assertThat(result.getContent().get(0).getTracks().get(1).getThumbnailImage()).isEqualTo(tracks.get(1).getThumbnailImage());

        assertThat(result.getContent().get(1).getGenreId()).isEqualTo(genre2.getId());
        assertThat(result.getContent().get(1).getGenreName()).isEqualTo(genre2.getGenreName());
        assertThat(result.getContent().get(1).getGenreImage()).isEqualTo(genre2.getGenreImage());
        assertThat(result.getContent().get(1).getTracks().size()).isEqualTo(2);
        assertThat(result.getContent().get(1).getTracks().get(0).getTrackId()).isEqualTo(tracks.get(0).getId());
        assertThat(result.getContent().get(1).getTracks().get(0).getTitle()).isEqualTo(tracks.get(0).getTitle());
        assertThat(result.getContent().get(1).getTracks().get(0).getUploaderName()).isEqualTo(user.getDisplayName());
        assertThat(result.getContent().get(1).getTracks().get(0).getThumbnailImage()).isEqualTo(tracks.get(0).getThumbnailImage());
        assertThat(result.getContent().get(1).getTracks().get(1).getTrackId()).isEqualTo(tracks.get(1).getId());
        assertThat(result.getContent().get(1).getTracks().get(1).getTitle()).isEqualTo(tracks.get(1).getTitle());
        assertThat(result.getContent().get(1).getTracks().get(1).getUploaderName()).isEqualTo(user.getDisplayName());
        assertThat(result.getContent().get(1).getTracks().get(1).getThumbnailImage()).isEqualTo(tracks.get(1).getThumbnailImage());
    }

    @Test
    @DisplayName("GET /genres/list - Unit Success")
    public void listAllGenresSuccess() {
        /* Given */

        // 장르 목록을 반환하도록 설정
        given(genreRepository.findAll()).willReturn(Arrays.asList(
                createMockGenre(1L, "Pop", "pop_image_url"),
                createMockGenre(2L, "RnB", "rnb_image_url"),
                createMockGenre(3L, "Jazz", "jazz_image_url")));

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
        User user = createMockUser(1L, "testUser", "Test Display", "test@example.com", "profileImageUrl", UserRole.USER,
                "socialId123");
        Genre genre = createMockGenre(1L, "Test Genre", "genreImageUrl");
        Track track = createMockTrack(1L, "Test Track", 300, true, "trackUrl", "trackImageUrl", "thumbnailImageUrl",
                user, genre);
        TrackDetail trackDetail = createMockTrackDetail(1L, "Test Prompt", track);
        Page<Track> trackPage = new PageImpl<>(List.of(track));

        given(genreRepository.findById(genreId)).willReturn(Optional.of(genre));
        given(trackRepository.findAllByGenreId(genreId, PageRequest.of(0, 30))).willReturn(trackPage);
        given(trackDetailRepository.findById(track.getId())).willReturn(Optional.of(trackDetail));

        /* When */
        GenreWithTracksResponseDto genreWithTracks = genreService.getGenreWithTracks(genreId, PageRequest.of(0, 30));

        /* Then */
        assertThat(genreWithTracks.getGenreId()).isEqualTo(genreId);
        assertThat(genreWithTracks.getGenreName()).isEqualTo(genre.getGenreName());
        assertThat(genreWithTracks.getGenreImage()).isEqualTo(genre.getGenreImage());
        assertThat(genreWithTracks.getTracks().getContent().size()).isEqualTo(1);
        assertThat(genreWithTracks.getTracks().getContent().get(0).getTrackId()).isEqualTo(track.getId());
        assertThat(genreWithTracks.getTracks().getContent().get(0).getTitle()).isEqualTo(track.getTitle());
        assertThat(genreWithTracks.getTracks().getContent().get(0).getDuration()).isEqualTo(track.getDuration());
        assertThat(genreWithTracks.getTracks().getContent().get(0).getHasLyrics()).isEqualTo(track.getHasLyrics());
        assertThat(genreWithTracks.getTracks().getContent().get(0).getTrackUrl()).isEqualTo(track.getTrackUrl());
        assertThat(genreWithTracks.getTracks().getContent().get(0).getTrackImage()).isEqualTo(track.getTrackImage());
        assertThat(genreWithTracks.getTracks().getContent().get(0).getThumbnailImage())
                .isEqualTo(track.getThumbnailImage());
        assertThat(genreWithTracks.getTracks().getContent().get(0).getPrompt()).isEqualTo(trackDetail.getPrompt());
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
