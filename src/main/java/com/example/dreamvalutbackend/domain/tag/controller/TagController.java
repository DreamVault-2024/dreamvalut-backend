package com.example.dreamvalutbackend.domain.tag.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dreamvalutbackend.domain.tag.controller.response.TagResponseDto;
import com.example.dreamvalutbackend.domain.tag.service.TagService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    // TODO: 추후에 Track 갯수가 많은 순으로 정렬하는 기능 추가
    @GetMapping("/list")
    public ResponseEntity<Page<TagResponseDto>> listAllTags(
            @PageableDefault(size = 6, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(tagService.listAllTags(pageable));
    }
}
