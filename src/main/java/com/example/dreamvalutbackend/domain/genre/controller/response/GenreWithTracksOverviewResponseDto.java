package com.example.dreamvalutbackend.domain.genre.controller.response;

import java.util.List;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackOverviewResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GenreWithTracksOverviewResponseDto {

    private Long genreId;
    private String genreName;
    private String genreImage;
    private List<TrackOverviewResponseDto> tracks;

    @Builder
    public GenreWithTracksOverviewResponseDto(Long genreId, String genreName, String genreImage,
            List<TrackOverviewResponseDto> tracks) {
        this.genreId = genreId;
        this.genreName = genreName;
        this.genreImage = genreImage;
        this.tracks = tracks;
    }

    public static GenreWithTracksOverviewResponseDto toDto(Genre genre, List<TrackOverviewResponseDto> tracks) {
        return GenreWithTracksOverviewResponseDto.builder()
                .genreId(genre.getId())
                .genreName(genre.getGenreName())
                .genreImage(genre.getGenreImage())
                .tracks(tracks)
                .build();
    }
}
