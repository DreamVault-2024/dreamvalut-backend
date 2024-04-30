package com.example.dreamvalutbackend.global.utils.search;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.dreamvalutbackend.domain.search.controller.document.TrackDocument;
import com.example.dreamvalutbackend.domain.search.controller.response.SearchTrackGenreResponseDto;
import com.example.dreamvalutbackend.domain.search.controller.response.SearchTrackResponseDto;
import com.example.dreamvalutbackend.domain.search.controller.response.SearchTrackTagResponseDto;

public class TrackDocumentUtil {

    public static SearchTrackResponseDto toDto(TrackDocument document, Map<String, List<String>> highlight, Long likes,
            Boolean likesFlag) {
        // SearchTrackGenreResponseDto 생성
        SearchTrackGenreResponseDto trackGenre = SearchTrackGenreResponseDto.builder()
                .genreId(document.getTrackGenre().getGenreId())
                .genreName(getHighlightedOrDefault(highlight, "track_genre.genre_name",
                        document.getTrackGenre().getGenreName()))
                .build();

        // SearchTrackTagResponseDto 리스트 생성
        List<SearchTrackTagResponseDto> trackTags = replaceToHighlightedTags(document.getTrackTags(),
                highlight.get("track_tags.tag_name"));

        // SearchTrackResponseDto 생성
        return SearchTrackResponseDto.builder()
                .id(document.getId())
                .title(getHighlightedOrDefault(highlight, "title", document.getTitle()))
                .uploaderName(getHighlightedOrDefault(highlight, "uploader_name", document.getUploaderName()))
                .prompt(getHighlightedOrDefault(highlight, "prompt", document.getPrompt()))
                .thumbnailImage(document.getThumbnailImage())
                .trackGenre(trackGenre)
                .trackTags(trackTags)
                .likes(likes)
                .likesFlag(likesFlag)
                .build();
    }

    // highlight에서 key에 해당하는 값이 있으면 반환하고, 없으면 defaultValue 반환
    private static String getHighlightedOrDefault(Map<String, List<String>> highlight, String key,
            String defaultValue) {
        return Optional.ofNullable(highlight.get(key))
                .map(list -> list.get(0))
                .orElse(defaultValue);
    }

    // trackTags에서 highlightedTags에 해당하는 값이 있으면 해당 값으로 대체하고, 없으면 tagName 그대로 반환
    private static List<SearchTrackTagResponseDto> replaceToHighlightedTags(
            List<TrackDocument.TrackTag> trackTags, List<String> highlightedTags) {
        if (highlightedTags == null) {
            return trackTags.stream()
                    .map(trackTag -> new SearchTrackTagResponseDto(trackTag.getTagId(), trackTag.getTagName()))
                    .collect(Collectors.toList());
        }

        return trackTags.stream()
                .map(trackTag -> {
                    String tagName = trackTag.getTagName();
                    for (String highlightedTag : highlightedTags) {
                        String strippedTag = stripEmTag(highlightedTag);
                        if (tagName.equals(strippedTag)) {
                            return new SearchTrackTagResponseDto(trackTag.getTagId(), highlightedTag);
                        }
                    }
                    return new SearchTrackTagResponseDto(trackTag.getTagId(), tagName);
                }).collect(Collectors.toList());
    }

    // replaceToHighlightedTags() 메서드에서 equals() 비교를 위해 <em> 태그 제거
    private static String stripEmTag(String highlightedTag) {
        return highlightedTag.replaceAll("<em>", "").replaceAll("</em>", "");
    }
}
