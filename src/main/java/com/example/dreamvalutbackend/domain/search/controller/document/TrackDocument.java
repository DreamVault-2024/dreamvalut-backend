package com.example.dreamvalutbackend.domain.search.controller.document;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrackDocument {

    private Long id;
    private String title;
    private String uploaderName;
    private String prompt;
    private TrackGenre trackGenre;
    private List<TrackTag> trackTags;
    private String thumbnailImage;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackGenre {

        private Long genreId;
        private String genreName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackTag {

        private Long tagId;
        private String tagName;
    }
}
