package com.example.dreamvalutbackend.domain.tag.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.dreamvalutbackend.domain.tag.controller.response.TagResponseDto;
import com.example.dreamvalutbackend.domain.tag.controller.response.TagWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.tag.domain.Tag;
import com.example.dreamvalutbackend.domain.tag.service.TagService;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;

public class TagControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tagController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("GET /tags/list - Unit Success")
    void listAllTagsSuccess() throws Exception {
        /* Given */
        List<TagResponseDto> tagResponseDtoList = List.of(
                new TagResponseDto(1L, "Tag1", "tag_image_url1"),
                new TagResponseDto(2L, "Tag2", "tag_image_url2"),
                new TagResponseDto(3L, "Tag3", "tag_image_url3"));

        Page<TagResponseDto> tagResponseDtoPage = new PageImpl<>(tagResponseDtoList, PageRequest.of(0, 6),
                tagResponseDtoList.size());

        given(tagService.listAllTags(any(Pageable.class))).willReturn(tagResponseDtoPage);

        /* When & Then */
        mockMvc.perform(get("/tags/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].tagId").value(1L))
                .andExpect(jsonPath("$.content[0].tagName").value("Tag1"))
                .andExpect(jsonPath("$.content[0].tagImage").value("tag_image_url1"))
                .andExpect(jsonPath("$.content[1].tagId").value(2L))
                .andExpect(jsonPath("$.content[1].tagName").value("Tag2"))
                .andExpect(jsonPath("$.content[1].tagImage").value("tag_image_url2"))
                .andExpect(jsonPath("$.content[2].tagId").value(3L))
                .andExpect(jsonPath("$.content[2].tagName").value("Tag3"))
                .andExpect(jsonPath("$.content[2].tagImage").value("tag_image_url3"));
    }

    @Test
    @DisplayName("GET /tags/{tag_id}/tracks - Unit Success")
    void getTagWithTracksSuccess() throws Exception {
        /* Given */

        Long tagId = 1L;

        List<TrackResponseDto> tagResponseDtoList = List.of(
                TrackResponseDto.builder()
                        .trackId(1L)
                        .title("Track1")
                        .uploaderName("Uploader1")
                        .duration(180)
                        .hasLyrics(true)
                        .trackUrl("track_url1")
                        .trackImage("track_image_url1")
                        .thumbnailImage("thumbnail_image_url1")
                        .prompt("prompt1")
                        .build(),
                TrackResponseDto.builder()
                        .trackId(2L)
                        .title("Track2")
                        .uploaderName("Uploader2")
                        .duration(210)
                        .hasLyrics(false)
                        .trackUrl("track_url2")
                        .trackImage("track_image_url2")
                        .thumbnailImage("thumbnail_image_url2")
                        .prompt("prompt2")
                        .build(),
                TrackResponseDto.builder()
                        .trackId(3L)
                        .title("Track3")
                        .uploaderName("Uploader3")
                        .duration(200)
                        .hasLyrics(true)
                        .trackUrl("track_url3")
                        .trackImage("track_image_url3")
                        .thumbnailImage("thumbnail_image_url3")
                        .prompt("prompt3")
                        .build());

        Page<TrackResponseDto> tagResponseDtoPage = new PageImpl<>(tagResponseDtoList, PageRequest.of(0, 30),
                tagResponseDtoList.size());

        TagWithTracksResponseDto tagWithTracksResponseDto = TagWithTracksResponseDto.builder()
                .tagId(tagId)
                .tagName("Tag1")
                .tagImage("tag_image_url1")
                .tracks(tagResponseDtoPage)
                .build();

        given(tagService.getTagWithTracks(any(Long.class), any(Pageable.class))).willReturn(tagWithTracksResponseDto);

        /* When & Then */
        mockMvc.perform(get("/tags/{tag_id}/tracks", tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(1))
                .andExpect(jsonPath("$.tagName").value("Tag1"))
                .andExpect(jsonPath("$.tagImage").value("tag_image_url1"))
                .andExpect(jsonPath("$.tracks.content[0].trackId").value(1))
                .andExpect(jsonPath("$.tracks.content[0].title").value("Track1"))
                .andExpect(jsonPath("$.tracks.content[0].uploaderName").value("Uploader1"))
                .andExpect(jsonPath("$.tracks.content[0].duration").value(180))
                .andExpect(jsonPath("$.tracks.content[0].hasLyrics").value(true))
                .andExpect(jsonPath("$.tracks.content[0].trackUrl").value("track_url1"))
                .andExpect(jsonPath("$.tracks.content[0].trackImage").value("track_image_url1"))
                .andExpect(jsonPath("$.tracks.content[0].thumbnailImage").value("thumbnail_image_url1"))
                .andExpect(jsonPath("$.tracks.content[0].prompt").value("prompt1"))
                .andExpect(jsonPath("$.tracks.content[1].trackId").value(2))
                .andExpect(jsonPath("$.tracks.content[1].title").value("Track2"))
                .andExpect(jsonPath("$.tracks.content[1].uploaderName").value("Uploader2"))
                .andExpect(jsonPath("$.tracks.content[1].duration").value(210))
                .andExpect(jsonPath("$.tracks.content[1].hasLyrics").value(false))
                .andExpect(jsonPath("$.tracks.content[1].trackUrl").value("track_url2"))
                .andExpect(jsonPath("$.tracks.content[1].trackImage").value("track_image_url2"))
                .andExpect(jsonPath("$.tracks.content[1].thumbnailImage").value("thumbnail_image_url2"))
                .andExpect(jsonPath("$.tracks.content[1].prompt").value("prompt2"))
                .andExpect(jsonPath("$.tracks.content[2].trackId").value(3))
                .andExpect(jsonPath("$.tracks.content[2].title").value("Track3"))
                .andExpect(jsonPath("$.tracks.content[2].uploaderName").value("Uploader3"))
                .andExpect(jsonPath("$.tracks.content[2].duration").value(200))
                .andExpect(jsonPath("$.tracks.content[2].hasLyrics").value(true))
                .andExpect(jsonPath("$.tracks.content[2].trackUrl").value("track_url3"))
                .andExpect(jsonPath("$.tracks.content[2].trackImage").value("track_image_url3"))
                .andExpect(jsonPath("$.tracks.content[2].thumbnailImage").value("thumbnail_image_url3"))
                .andExpect(jsonPath("$.tracks.content[2].prompt").value("prompt3"))
                .andExpect(jsonPath("$.tracks.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.tracks.pageable.pageSize").value(30))
                .andExpect(jsonPath("$.tracks.last").value(true))
                .andExpect(jsonPath("$.tracks.totalPages").value(1))
                .andExpect(jsonPath("$.tracks.totalElements").value(3))
                .andExpect(jsonPath("$.tracks.number").value(0))
                .andExpect(jsonPath("$.tracks.size").value(30))
                .andExpect(jsonPath("$.tracks.first").value(true))
                .andExpect(jsonPath("$.tracks.numberOfElements").value(3))
                .andExpect(jsonPath("$.tracks.empty").value(false));
    }
}
