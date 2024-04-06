package com.example.dreamvalutbackend.domain.genre.controller.response;

import org.springframework.data.domain.Page;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GenreWithTracksResponseDto {

    private Long genreId;
    private String genreName;
    private String genreImage;
    private Page<TrackResponseDto> tracks;

    @Builder
    public GenreWithTracksResponseDto(Long genreId, String genreName, String genreImage,
            Page<TrackResponseDto> tracks) {
        this.genreId = genreId;
        this.genreName = genreName;
        this.genreImage = genreImage;
        this.tracks = tracks;
    }

    public static GenreWithTracksResponseDto toDto(Genre genre, Page<TrackResponseDto> tracks) {
        return GenreWithTracksResponseDto.builder()
                .genreId(genre.getId())
                .genreName(genre.getGenreName())
                .genreImage(genre.getGenreImage())
                .tracks(tracks)
                .build();
    }
}
