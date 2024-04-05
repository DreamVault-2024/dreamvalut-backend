package com.example.dreamvalutbackend.domain.genre.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<GenreResponseDto> listAllGenres() {
        return genreRepository.findAll().stream()
                .map(genre -> GenreResponseDto.toDto(genre))
                .collect(Collectors.toList());
    }
}
