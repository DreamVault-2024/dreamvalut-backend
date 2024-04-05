package com.example.dreamvalutbackend.domain.genre.service;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;

public class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

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
}
