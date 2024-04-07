package com.example.dreamvalutbackend.domain.tag.service;

import java.util.Arrays;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.tag.controller.response.TagResponseDto;
import com.example.dreamvalutbackend.domain.tag.domain.Tag;
import com.example.dreamvalutbackend.domain.tag.domain.TrackTag;
import com.example.dreamvalutbackend.domain.tag.repository.TagRepository;
import com.example.dreamvalutbackend.domain.tag.repository.TrackTagRepository;
import com.example.dreamvalutbackend.domain.track.domain.Track;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TrackTagRepository trackTagRepository;

    public Page<TagResponseDto> listAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(TagResponseDto::toDto);
    }

    @Transactional
    public void associateTrackWithTags(Track track, String[] tagNames) {
        // 태그들을 순회하며 태그를 찾아서 없으면 생성하고, TrackTag를 생성하여 저장
        Arrays.stream(tagNames).forEach(tagName -> {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName, "default tag image")));
            trackTagRepository.save(new TrackTag(track, tag));
        });
    }
}
