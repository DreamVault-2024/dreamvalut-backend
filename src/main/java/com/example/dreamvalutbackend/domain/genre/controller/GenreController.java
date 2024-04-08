package com.example.dreamvalutbackend.domain.genre.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.genre.service.GenreService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<Page<GenreWithTracksOverviewResponseDto>> getGenresWithTracksOverview(
            @PageableDefault(page = 0, size = 4, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(genreService.getGenresWithTracksOverview(pageable));
    }

    @GetMapping("/list")
    public ResponseEntity<List<GenreResponseDto>> listAllGenres() {
        return ResponseEntity.ok(genreService.listAllGenres());
    }

    @GetMapping("/{genre_id}/tracks")
    public ResponseEntity<GenreWithTracksResponseDto> getGenreWithTracks(@PathVariable("genre_id") Long genreId,
            @PageableDefault(page = 0, size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(genreService.getGenreWithTracks(genreId, pageable));
    }
}
