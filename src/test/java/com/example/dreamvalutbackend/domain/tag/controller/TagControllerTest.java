package com.example.dreamvalutbackend.domain.tag.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
import com.example.dreamvalutbackend.domain.tag.domain.Tag;
import com.example.dreamvalutbackend.domain.tag.domain.TrackTag;
import com.example.dreamvalutbackend.domain.tag.repository.TagRepository;
import com.example.dreamvalutbackend.domain.tag.repository.TrackTagRepository;
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
public class TagControllerTest {

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
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TrackTagRepository trackTagRepository;

    private User user;
    private Genre genre;
    private List<Tag> tags;
    private List<Track> tracks;
    private List<TrackDetail> trackDetails;
    private List<TrackTag> trackTags;

    @BeforeEach
    public void setUp() {
        // 기본 유저 데이터 생성
        user = userRepository.save(createUser("testUserName", "testDisplayName", "testEmail", "testProfileImage",
                UserRole.USER, "testSocialId"));

        // 기본 장르 데이터 생성
        genre = genreRepository.save(createGenre("defaultGenre", "defaultGenreImage"));

        // 기본 태그 데이터 생성
        tags = List.of(
                tagRepository.save(createTag("defaultTag1", "defaultTagImage1")),
                tagRepository.save(createTag("defaultTag2", "defaultTagImage2")),
                tagRepository.save(createTag("defaultTag3", "defaultTagImage3")),
                tagRepository.save(createTag("defaultTag4", "defaultTagImage4")));

        // 기본 트랙 데이터 생성
        tracks = List.of(
                trackRepository.save(createTrack("defaultTrack1", 120, true, "defaultTrackUrl1", "defaultTrackImage1",
                        "defaultThumbnailImage1", user, genre)),
                trackRepository.save(createTrack("defaultTrack2", 180, false, "defaultTrackUrl2", "defaultTrackImage2",
                        "defaultThumbnailImage2", user, genre)),
                trackRepository.save(createTrack("defaultTrack3", 150, true, "defaultTrackUrl3", "defaultTrackImage3",
                        "defaultThumbnailImage3", user, genre)),
                trackRepository.save(createTrack("defaultTrack4", 200, false, "defaultTrackUrl4", "defaultTrackImage4",
                        "defaultThumbnailImage4", user, genre)));

        // 기본 트랙 상세 데이터 생성
        trackDetails = List.of(
                trackDetailRepository.save(createTrackDetail(tracks.get(0), "defaultPrompt1")),
                trackDetailRepository.save(createTrackDetail(tracks.get(1), "defaultPrompt2")),
                trackDetailRepository.save(createTrackDetail(tracks.get(2), "defaultPrompt3")),
                trackDetailRepository.save(createTrackDetail(tracks.get(3), "defaultPrompt4")));

        // 기본 트랙 태그 데이터 생성
        trackTags = List.of(
                trackTagRepository.save(createTrackTag(tracks.get(0), tags.get(0))),
                trackTagRepository.save(createTrackTag(tracks.get(1), tags.get(0))),
                trackTagRepository.save(createTrackTag(tracks.get(2), tags.get(0))),
                trackTagRepository.save(createTrackTag(tracks.get(3), tags.get(0))));
    }

    @AfterEach
    public void tearDown() {
        trackTagRepository.deleteAll();
        trackDetailRepository.deleteAll();
        trackRepository.deleteAll();
        tagRepository.deleteAll();
        genreRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /tags/list - Integration Success")
    @Transactional
    public void listAllTagsSuccess() throws Exception {
        mockMvc.perform(get("/tags/list").param("page", "0").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].tagId").value(1))
                .andExpect(jsonPath("$.content[0].tagName").value("defaultTag1"))
                .andExpect(jsonPath("$.content[0].tagImage").value("defaultTagImage1"))
                .andExpect(jsonPath("$.content[1].tagId").value(2))
                .andExpect(jsonPath("$.content[1].tagName").value("defaultTag2"))
                .andExpect(jsonPath("$.content[1].tagImage").value("defaultTagImage2"))
                .andExpect(jsonPath("$.content[2].tagId").value(3))
                .andExpect(jsonPath("$.content[2].tagName").value("defaultTag3"))
                .andExpect(jsonPath("$.content[2].tagImage").value("defaultTagImage3"))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(3))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.pageable.offset").value(0))
                .andExpect(jsonPath("$.pageable.paged").value(true))
                .andExpect(jsonPath("$.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.last").value(false))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.sort.sorted").value(true))
                .andExpect(jsonPath("$.sort.unsorted").value(false))
                .andExpect(jsonPath("$.sort.empty").value(false))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.numberOfElements").value(3))
                .andExpect(jsonPath("$.empty").value(false));
    }

    @Test
    @DisplayName("GET /tags/{tag_id}/tracks - Integration Success")
    @Transactional
    public void getTagWithTracksSuccess() throws Exception {
        mockMvc.perform(get("/tags/{tag_id}/tracks", tags.get(0).getId()).param("page", "0").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(tags.get(0).getId()))
                .andExpect(jsonPath("$.tagName").value(tags.get(0).getTagName()))
                .andExpect(jsonPath("$.tagImage").value(tags.get(0).getTagImage()))
                .andExpect(jsonPath("$.tracks.content[0].trackId").value(tracks.get(3).getId()))
                .andExpect(jsonPath("$.tracks.content[0].title").value(tracks.get(3).getTitle()))
                .andExpect(jsonPath("$.tracks.content[0].uploaderName").value(user.getDisplayName()))
                .andExpect(jsonPath("$.tracks.content[0].duration").value(tracks.get(3).getDuration()))
                .andExpect(jsonPath("$.tracks.content[0].hasLyrics").value(tracks.get(3).getHasLyrics()))
                .andExpect(jsonPath("$.tracks.content[0].trackUrl").value(tracks.get(3).getTrackUrl()))
                .andExpect(jsonPath("$.tracks.content[0].trackImage").value(tracks.get(3).getTrackImage()))
                .andExpect(jsonPath("$.tracks.content[0].thumbnailImage").value(tracks.get(3).getThumbnailImage()))
                .andExpect(jsonPath("$.tracks.content[0].prompt").value(trackDetails.get(3).getPrompt()))
                .andExpect(jsonPath("$.tracks.content[1].trackId").value(tracks.get(2).getId()))
                .andExpect(jsonPath("$.tracks.content[1].title").value(tracks.get(2).getTitle()))
                .andExpect(jsonPath("$.tracks.content[1].uploaderName").value(user.getDisplayName()))
                .andExpect(jsonPath("$.tracks.content[1].duration").value(tracks.get(2).getDuration()))
                .andExpect(jsonPath("$.tracks.content[1].hasLyrics").value(tracks.get(2).getHasLyrics()))
                .andExpect(jsonPath("$.tracks.content[1].trackUrl").value(tracks.get(2).getTrackUrl()))
                .andExpect(jsonPath("$.tracks.content[1].trackImage").value(tracks.get(2).getTrackImage()))
                .andExpect(jsonPath("$.tracks.content[1].thumbnailImage").value(tracks.get(2).getThumbnailImage()))
                .andExpect(jsonPath("$.tracks.content[1].prompt").value(trackDetails.get(2).getPrompt()))
                .andExpect(jsonPath("$.tracks.content[2].trackId").value(tracks.get(1).getId()))
                .andExpect(jsonPath("$.tracks.content[2].title").value(tracks.get(1).getTitle()))
                .andExpect(jsonPath("$.tracks.content[2].uploaderName").value(user.getDisplayName()))
                .andExpect(jsonPath("$.tracks.content[2].duration").value(tracks.get(1).getDuration()))
                .andExpect(jsonPath("$.tracks.content[2].hasLyrics").value(tracks.get(1).getHasLyrics()))
                .andExpect(jsonPath("$.tracks.content[2].trackUrl").value(tracks.get(1).getTrackUrl()))
                .andExpect(jsonPath("$.tracks.content[2].trackImage").value(tracks.get(1).getTrackImage()))
                .andExpect(jsonPath("$.tracks.content[2].thumbnailImage").value(tracks.get(1).getThumbnailImage()))
                .andExpect(jsonPath("$.tracks.content[2].prompt").value(trackDetails.get(1).getPrompt()))
                .andExpect(jsonPath("$.tracks.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.tracks.pageable.pageSize").value(3))
                .andExpect(jsonPath("$.tracks.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.tracks.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.tracks.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.tracks.pageable.offset").value(0))
                .andExpect(jsonPath("$.tracks.pageable.paged").value(true))
                .andExpect(jsonPath("$.tracks.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.tracks.last").value(false))
                .andExpect(jsonPath("$.tracks.totalElements").value(4))
                .andExpect(jsonPath("$.tracks.totalPages").value(2))
                .andExpect(jsonPath("$.tracks.number").value(0))
                .andExpect(jsonPath("$.tracks.size").value(3))
                .andExpect(jsonPath("$.tracks.first").value(true))
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

    private Tag createTag(String tagName, String tagImage) {
        return new Tag(tagName, tagImage);
    }

    private TrackTag createTrackTag(Track track, Tag tag) {
        return new TrackTag(track, tag);
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
