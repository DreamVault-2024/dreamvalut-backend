package com.example.dreamvalutbackend.domain.genre.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.service.GenreService;

public class GenreControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreController genreController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(genreController).build();
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
}
