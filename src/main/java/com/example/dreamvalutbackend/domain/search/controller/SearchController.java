package com.example.dreamvalutbackend.domain.search.controller;

import java.io.IOException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dreamvalutbackend.domain.search.controller.response.CustomPage;
import com.example.dreamvalutbackend.domain.search.controller.response.SearchTrackResponseDto;
import com.example.dreamvalutbackend.domain.search.service.SearchService;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<CustomPage<SearchTrackResponseDto>> searchTrack(
            @RequestParam(value = "query", required = true) String query,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) throws IOException {
        return ResponseEntity.ok(searchService.searchTrack(query, pageable, userDetailPrincipal.getUserId()));
    }
}
