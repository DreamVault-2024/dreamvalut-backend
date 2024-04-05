package com.example.dreamvalutbackend.domain.genre.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dreamvalutbackend.domain.genre.controller.response.GenreResponseDto;
import com.example.dreamvalutbackend.domain.genre.service.GenreService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/list")
    public ResponseEntity<List<GenreResponseDto>> listAllGenres() {
        return ResponseEntity.ok(genreService.listAllGenres());
    }
}
