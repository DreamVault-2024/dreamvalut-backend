package com.example.dreamvalutbackend.domain.tag.service;

import java.util.Arrays;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.like.repository.LikeRepository;
import com.example.dreamvalutbackend.domain.tag.controller.response.TagResponseDto;
import com.example.dreamvalutbackend.domain.tag.controller.response.TagWithTracksResponseDto;
import com.example.dreamvalutbackend.domain.tag.domain.Tag;
import com.example.dreamvalutbackend.domain.tag.domain.TrackTag;
import com.example.dreamvalutbackend.domain.tag.repository.TagRepository;
import com.example.dreamvalutbackend.domain.tag.repository.TrackTagRepository;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TrackTagRepository trackTagRepository;
    private final TrackDetailRepository trackDetailRepository;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public Page<TagResponseDto> listAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(TagResponseDto::toDto);
    }

    @Transactional(readOnly = true)
    public TagWithTracksResponseDto getTagWithTracks(Long tagId, Pageable pageable) {
        // tagId로 해당하는 태그 가져오기
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        // 해당 태그의 트랙들 찾기
        Page<TrackResponseDto> tracks = trackTagRepository.findByTagId(tagId, pageable)
                .map(trackTag -> {
                    Track track = trackTag.getTrack();
                    TrackDetail trackDetail = trackDetailRepository.findById(track.getId())
                            .orElseThrow(() -> new IllegalArgumentException("TrackDetail not found"));
                    Long likes = likeRepository.countByTrackId(track.getId());
                    return TrackResponseDto.toDto(track, trackDetail, likes);
                });

        // 태그와 트랙들을 DTO로 변환하여 반환
        return TagWithTracksResponseDto.toDto(tag, tracks);
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
