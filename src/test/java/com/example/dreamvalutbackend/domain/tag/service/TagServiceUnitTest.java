package com.example.dreamvalutbackend.domain.tag.service;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.tag.controller.response.TagResponseDto;
import com.example.dreamvalutbackend.domain.tag.controller.response.TagWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.tag.domain.Tag;
import com.example.dreamvalutbackend.domain.tag.domain.TrackTag;
import com.example.dreamvalutbackend.domain.tag.repository.TagRepository;
import com.example.dreamvalutbackend.domain.tag.repository.TrackTagRepository;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.domain.UserRole;

public class TagServiceUnitTest {

    @Mock
    private TagRepository tagRepository;
    @Mock
    private TrackTagRepository trackTagRepository;
    @Mock
    private TrackDetailRepository trackDetailRepository;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("GET /tags/list - Unit Success")
    void listAllTagsSuccess() {
        /* Given */

        PageRequest pageable = PageRequest.of(0, 6);
        Page<Tag> tags = new PageImpl<>(List.of(
                createMockTag(1L, "Tag1", "tag1_image_url"),
                createMockTag(2L, "Tag2", "tag2_image_url"),
                createMockTag(3L, "Tag3", "tag3_image_url")));

        given(tagRepository.findAll(pageable)).willReturn(tags);

        /* When */
        Page<TagResponseDto> result = tagService.listAllTags(pageable);

        /* Then */
        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getContent().get(0).getTagName()).isEqualTo("Tag1");
        assertThat(result.getContent().get(0).getTagImage()).isEqualTo("tag1_image_url");
        assertThat(result.getContent().get(1).getTagName()).isEqualTo("Tag2");
        assertThat(result.getContent().get(1).getTagImage()).isEqualTo("tag2_image_url");
        assertThat(result.getContent().get(2).getTagName()).isEqualTo("Tag3");
        assertThat(result.getContent().get(2).getTagImage()).isEqualTo("tag3_image_url");
    }

    @Test
    @DisplayName("GET /tags/{tag_id}/tracks - Unit Success")
    void getTagWithTracksSuccess() {
        /* Given */

        // 요청할 Tag ID
        Long tagId = 1L;

        // Mock Tag, Track, TrackDetail 객체 생성
        Tag tag = createMockTag(tagId, "Test Tag", "test_tag_image_url");
        User user = createMockUser(1L, "Test User", "Test User", "test@example.com", "https://example.com/profile.jpg",
                UserRole.USER, "1234567890");
        Genre genre = createMockGenre(1L, "Test Genre", "https://example.com/genre/test.jpg");
        Track track = createMockTrack(1L, "Test Track", 180, true,
                "https://example.com/track/test.mp3", "https://example.com/track/test.jpg",
                "https://example.com/track/test_thumbnail.jpg", user, genre);
        Page<TrackTag> trackTagPage = new PageImpl<>(List.of(createMockTrackTag(1L, track, tag)));
        TrackDetail trackDetail = createMockTrackDetail(1L, "Test Prompt", track);

        given(tagRepository.findById(tagId)).willReturn(Optional.of(tag));
        given(trackTagRepository.findByTagId(tagId, PageRequest.of(0, 30))).willReturn(trackTagPage);
        given(trackDetailRepository.findById(track.getId())).willReturn(Optional.of(trackDetail));

        /* When */
        TagWithTracksResponseDto result = tagService.getTagWithTracks(tagId, PageRequest.of(0, 30));

        /* Then */
        assertThat(result.getTagId()).isEqualTo(1L);
        assertThat(result.getTagName()).isEqualTo("Test Tag");
        assertThat(result.getTagImage()).isEqualTo("test_tag_image_url");
        assertThat(result.getTracks().getSize()).isEqualTo(1);
        assertThat(result.getTracks().getContent().get(0).getTrackId()).isEqualTo(1L);
        assertThat(result.getTracks().getContent().get(0).getTitle()).isEqualTo("Test Track");
        assertThat(result.getTracks().getContent().get(0).getUploaderName()).isEqualTo("Test User");
        assertThat(result.getTracks().getContent().get(0).getDuration()).isEqualTo(180);
        assertThat(result.getTracks().getContent().get(0).getHasLyrics()).isTrue();
        assertThat(result.getTracks().getContent().get(0).getTrackUrl())
                .isEqualTo("https://example.com/track/test.mp3");
        assertThat(result.getTracks().getContent().get(0).getTrackImage())
                .isEqualTo("https://example.com/track/test.jpg");
        assertThat(result.getTracks().getContent().get(0).getThumbnailImage())
                .isEqualTo("https://example.com/track/test_thumbnail.jpg");
        assertThat(result.getTracks().getContent().get(0).getPrompt()).isEqualTo("Test Prompt");
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

    private Tag createMockTag(Long id, String tagName, String tagImage) {
        try {
            Constructor<Tag> constructor = Tag.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Tag tag = constructor.newInstance();

            setField(tag, "id", id);
            setField(tag, "tagName", tagName);
            setField(tag, "tagImage", tagImage);

            return tag;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock Tag", e);
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

    private TrackTag createMockTrackTag(Long id, Track track, Tag tag) {
        try {
            Constructor<TrackTag> constructor = TrackTag.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            TrackTag trackTag = constructor.newInstance();

            setField(trackTag, "id", id);
            setField(trackTag, "track", track);
            setField(trackTag, "tag", tag);

            return trackTag;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock TrackTag", e);
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
