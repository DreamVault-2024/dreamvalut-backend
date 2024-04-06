package com.example.dreamvalutbackend.domain.genre.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.genre.service.GenreService;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;

public class GenreControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreController genreController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(genreController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("GET /genres/list - Unit Success")
    void listAllGenresSuccess() throws Exception {
        /* Given */
        given(genreService.listAllGenres()).willReturn(Arrays.asList(
                new GenreResponseDto(1L, "Pop", "pop_image_url"),
                new GenreResponseDto(2L, "RnB", "rnb_image_url"),
                new GenreResponseDto(3L, "Jazz", "jazz_image_url")));

        /* When & Then */
        mockMvc.perform(get("/genres/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genreName").value("Pop"))
                .andExpect(jsonPath("$[0].genreImage").value("pop_image_url"))
                .andExpect(jsonPath("$[1].genreName").value("RnB"))
                .andExpect(jsonPath("$[1].genreImage").value("rnb_image_url"))
                .andExpect(jsonPath("$[2].genreName").value("Jazz"))
                .andExpect(jsonPath("$[2].genreImage").value("jazz_image_url"));
    }

    @Test
    @DisplayName("GET /genres/{genre_id}/tracks - Unit Success")
    void getGenreWithTracksSuccess() throws Exception {
        /* Given */

        // 요청할 Genre ID
        Long genreId = 1L;

        // TrackResponseDto 객체 생성
        TrackResponseDto trackResponseDto1 = TrackResponseDto.builder()
                .trackId(1L)
                .title("Dummy Title 1")
                .uploaderName("Dummy Uploader 1")
                .duration(300)
                .hasLyrics(true)
                .trackUrl("http://dummyurl1.com")
                .trackImage("http://dummyimage1.com")
                .thumbnailImage("http://dummythumbnail1.com")
                .prompt("Dummy Prompt 1")
                .build();
        TrackResponseDto trackResponseDto2 = TrackResponseDto.builder()
                .trackId(2L)
                .title("Dummy Title 2")
                .uploaderName("Dummy Uploader 2")
                .duration(200)
                .hasLyrics(false)
                .trackUrl("http://dummyurl2.com")
                .trackImage("http://dummyimage2.com")
                .thumbnailImage("http://dummythumbnail2.com")
                .prompt("Dummy Prompt 2")
                .build();

        // TrackResponseDto 객체 리스트
        List<TrackResponseDto> trackResponseDtoList = Arrays.asList(trackResponseDto1, trackResponseDto2);

        // Page 객체 생성
        Page<TrackResponseDto> dummyTracks = new PageImpl<>(trackResponseDtoList, PageRequest.of(0, 10),
                trackResponseDtoList.size());

        // GenreWithTracksResponseDto 객체 생성
        GenreWithTracksResponseDto genreWithTracksResponseDto = GenreWithTracksResponseDto.builder()
                .genreId(genreId)
                .genreName("Pop")
                .genreImage("pop_image_url")
                .tracks(dummyTracks)
                .build();

        given(genreService.getGenreWithTracks(eq(genreId), any(PageRequest.class)))
                .willReturn(genreWithTracksResponseDto);

        /* When & Then */
        mockMvc.perform(get("/genres/{genre_id}/tracks", genreId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genreId").value(1L))
                .andExpect(jsonPath("$.genreName").value("Pop"))
                .andExpect(jsonPath("$.genreImage").value("pop_image_url"))
                .andExpect(jsonPath("$.tracks.content[0].trackId").value(1L))
                .andExpect(jsonPath("$.tracks.content[0].title").value("Dummy Title 1"))
                .andExpect(jsonPath("$.tracks.content[0].uploaderName").value("Dummy Uploader 1"))
                .andExpect(jsonPath("$.tracks.content[0].duration").value(300))
                .andExpect(jsonPath("$.tracks.content[0].hasLyrics").value(true))
                .andExpect(jsonPath("$.tracks.content[0].trackUrl").value("http://dummyurl1.com"))
                .andExpect(jsonPath("$.tracks.content[0].trackImage").value("http://dummyimage1.com"))
                .andExpect(jsonPath("$.tracks.content[0].thumbnailImage").value("http://dummythumbnail1.com"))
                .andExpect(jsonPath("$.tracks.content[0].prompt").value("Dummy Prompt 1"))
                .andExpect(jsonPath("$.tracks.content[1].trackId").value(2L))
                .andExpect(jsonPath("$.tracks.content[1].title").value("Dummy Title 2"))
                .andExpect(jsonPath("$.tracks.content[1].uploaderName").value("Dummy Uploader 2"))
                .andExpect(jsonPath("$.tracks.content[1].duration").value(200))
                .andExpect(jsonPath("$.tracks.content[1].hasLyrics").value(false))
                .andExpect(jsonPath("$.tracks.content[1].trackUrl").value("http://dummyurl2.com"))
                .andExpect(jsonPath("$.tracks.content[1].trackImage").value("http://dummyimage2.com"))
                .andExpect(jsonPath("$.tracks.content[1].thumbnailImage").value("http://dummythumbnail2.com"))
                .andExpect(jsonPath("$.tracks.content[1].prompt").value("Dummy Prompt 2"))
                .andExpect(jsonPath("$.tracks.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.tracks.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.tracks.pageable.sort.empty").value(true))
                .andExpect(jsonPath("$.tracks.pageable.sort.unsorted").value(true))
                .andExpect(jsonPath("$.tracks.pageable.sort.sorted").value(false))
                .andExpect(jsonPath("$.tracks.pageable.offset").value(0))
                .andExpect(jsonPath("$.tracks.pageable.paged").value(true))
                .andExpect(jsonPath("$.tracks.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.tracks.totalPages").value(1))
                .andExpect(jsonPath("$.tracks.last").value(true))
                .andExpect(jsonPath("$.tracks.totalElements").value(2))
                .andExpect(jsonPath("$.tracks.size").value(10))
                .andExpect(jsonPath("$.tracks.sort.empty").value(true))
                .andExpect(jsonPath("$.tracks.sort.unsorted").value(true))
                .andExpect(jsonPath("$.tracks.sort.sorted").value(false))
                .andExpect(jsonPath("$.tracks.number").value(0))
                .andExpect(jsonPath("$.tracks.first").value(true))
                .andExpect(jsonPath("$.tracks.numberOfElements").value(2))
                .andExpect(jsonPath("$.tracks.empty").value(false));
    }
}
