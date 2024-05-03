package com.example.dreamvalutbackend.domain.search.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.dreamvalutbackend.domain.like.repository.LikeRepository;
import com.example.dreamvalutbackend.domain.search.controller.document.TrackDocument;
import com.example.dreamvalutbackend.domain.search.controller.response.CustomPage;
import com.example.dreamvalutbackend.domain.search.controller.response.SearchTrackResponseDto;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;
import com.example.dreamvalutbackend.global.utils.search.TrackDocumentUtil;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public CustomPage<SearchTrackResponseDto> searchTrack(String query, Pageable pageable, Long userId)
            throws IOException {

        // userId로 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // page, size로 from 설정
        int from = pageable.getPageNumber() * pageable.getPageSize();

        // MultiMatchQuery 생성
        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(builder -> builder
                .query(query)
                .fields(List.of(
                        "title",
                        "uploader_name",
                        "prompt",
                        "track_genre.genre_name",
                        "track_tags.tag_name"))
                .fuzziness("AUTO"));

        // SearchRequest 생성
        SearchRequest request = SearchRequest.of(builder -> builder
                .index("tracks-index")
                .query(q -> q.multiMatch(multiMatchQuery))
                .from(from)
                .size(pageable.getPageSize())
                .source(SourceConfig.of(s -> s
                        .filter(f -> f.excludes(List.of("@timestamp")))))
                .highlight(h -> h
                        .fields("*", f -> f)
                        .numberOfFragments(0)
                        .requireFieldMatch(true)));

        // ElasticsearchClient로 검색 요청
        SearchResponse<TrackDocument> response = elasticsearchClient.search(request, TrackDocument.class);

        // 검색 결과를 SearchTrackResponseDto로 변환
        List<SearchTrackResponseDto> searchTrackResponseDtos = response.hits().hits().stream()
                .map(hit -> {
                    Long likes = likeRepository.countByTrackId(hit.source().getId());
                    Boolean likesFlag = likeRepository.existsByUserIdAndTrackId(user.getUserId(), hit.source().getId());
                    return TrackDocumentUtil.toDto(hit.source(), hit.highlight(), likes, likesFlag);
                }).collect(Collectors.toList());

        // Pageable 생성
        Pageable responsePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("score").descending());

        // CustomPage 생성
        return new CustomPage<>(searchTrackResponseDtos, responsePageable, response.hits().total().value());
    }
}
