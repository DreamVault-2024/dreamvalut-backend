package com.example.dreamvalutbackend.domain.genre.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.genre.controller.response.GenreWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.genre.service.GenreService;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    @Operation(summary = "장르별 곡 3개씩 가져오기")
    public ResponseEntity<Page<GenreWithTracksOverviewResponseDto>> getGenresWithTracksOverview(
            @PageableDefault(page = 0, size = 4, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(genreService.getGenresWithTracksOverview(pageable));
    }

    @GetMapping("/list")
    @Operation(summary = "모든 장르 리스트 가져오기")
    public ResponseEntity<List<GenreResponseDto>> listAllGenres() {
        return ResponseEntity.ok(genreService.listAllGenres());
    }

    @GetMapping("/{genre_id}/tracks")
    @Operation(summary = "특정 장르의 모든 곡 가져오기")
    public ResponseEntity<GenreWithTracksResponseDto> getGenreWithTracks(@PathVariable("genre_id") Long genreId,
            @PageableDefault(page = 0, size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {
        return ResponseEntity.ok(genreService.getGenreWithTracks(genreId, pageable, userDetailPrincipal.getUserId()));
    }
}
