package com.example.dreamvalutbackend.domain.genre.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.TrackOverviewResponseDto;
import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final TrackRepository trackRepository;
    private final TrackDetailRepository trackDetailRepository;

    public List<GenreWithTracksOverviewResponseDto> getGenresWithTracksOverview(Pageable pageable) {
        // 장르들 가져오기
        Page<Genre> genres = genreRepository.findAll(pageable);

        // 각 장르에 해당하는 트랙들 간단한 정보 가져오기
        List<GenreWithTracksOverviewResponseDto> genreWithTracksResponseDtos = genres.stream().map(genre -> {
            // 장르에 해당하는 트랙들 3개 가져오기
            Page<Track> tracks = trackRepository.findAllByGenreId(genre.getId(),
                    PageRequest.of(0, 3, Sort.by("id").descending()));

            // 트랙들의 간단한 정보만 TrackOverviewResponseDto로 변환
            List<TrackOverviewResponseDto> trackDtos = tracks.getContent().stream()
                    .map(TrackOverviewResponseDto::toDto)
                    .collect(Collectors.toList());

            // 장르와 트랙들의 간단한 정보를 GenreWithTracksOverviewResponseDto로 변환
            return GenreWithTracksOverviewResponseDto.toDto(genre, trackDtos);
        }).collect(Collectors.toList());

        return genreWithTracksResponseDtos;
    }

    public List<GenreResponseDto> listAllGenres() {
        return genreRepository.findAll().stream()
                .map(genre -> GenreResponseDto.toDto(genre))
                .collect(Collectors.toList());
    }

    public GenreWithTracksResponseDto getGenreWithTracks(Long genreId, Pageable pageable) {
        // genreId로 해당하는 장르 가져오기
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found"));

        // 해당 장르의 트랙들 찾기
        Page<Track> tracks = trackRepository.findAllByGenreId(genreId, pageable);

        // 트랙들의 상세 정보 가져오기
        Page<TrackResponseDto> trackResponseDtos = tracks.map(track -> {
            TrackDetail trackDetail = trackDetailRepository.findById(track.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Track detail not found"));
            return TrackResponseDto.toDto(track, trackDetail);
        });

        return GenreWithTracksResponseDto.toDto(genre, trackResponseDtos);
    }
}
