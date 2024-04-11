package com.example.dreamvalutbackend.domain.genre.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackOverviewResponseDto;
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

    @Transactional(readOnly = true)
    public Page<GenreWithTracksOverviewResponseDto> getGenresWithTracksOverview(Pageable pageable) {
        // 장르들 가져오기
        Page<Genre> genres = genreRepository.findAll(pageable);

        return genres.map(genre -> {
            // 장르에 해당하는 트랙들 가져오기
            List<Track> tracks = trackRepository.findAllByGenreId(genre.getId(),
                    PageRequest.of(0, 3, Sort.by("id").descending())).getContent();

            // 트랙들의 Overview 정보 가져오기
            List<TrackOverviewResponseDto> trackDtos = tracks.stream()
                    .map(TrackOverviewResponseDto::toDto)
                    .collect(Collectors.toList());
            
            // 장르와 트랙들의 Overview 정보를 DTO로 변환
            return GenreWithTracksOverviewResponseDto.toDto(genre, trackDtos);
        });
    }

    @Transactional(readOnly = true)
    public List<GenreResponseDto> listAllGenres() {
        return genreRepository.findAll().stream()
                .map(genre -> GenreResponseDto.toDto(genre))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
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
