package com.example.dreamvalutbackend.domain.genre.controller.response;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GenreResponseDto {

    private Long genreId;
    private String genreName;
    private String genreImage;

    @Builder
    public GenreResponseDto(Long genreId, String genreName, String genreImage) {
        this.genreId = genreId;
        this.genreName = genreName;
        this.genreImage = genreImage;
    }

    public static GenreResponseDto toDto(Genre genre) {
        return GenreResponseDto.builder()
                .genreId(genre.getId())
                .genreName(genre.getGenreName())
                .genreImage(genre.getGenreImage())
                .build();
    }
}
