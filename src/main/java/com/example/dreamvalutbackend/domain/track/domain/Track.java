package com.example.dreamvalutbackend.domain.track.domain;

import java.util.ArrayList;
import java.util.List;

import com.example.dreamvalutbackend.domain.common.BaseTimeEntity;
import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.playlist.domain.PlaylistTrack;
import com.example.dreamvalutbackend.domain.tag.domain.TrackTag;
import com.example.dreamvalutbackend.domain.user.domain.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tracks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Track extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    // @Column(nullable = false)
    // private Integer duration;

    @Column(nullable = false)
    private Boolean hasLyrics;

    @Column(nullable = false)
    private String trackUrl;

    @Column(nullable = false)
    private String trackImage;

    @Column(nullable = false)
    private String thumbnailImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    // @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval =
    // true, fetch = FetchType.LAZY)
    // private List<TrackTag> trackTags = new ArrayList<>();

    // @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval =
    // true, fetch = FetchType.LAZY)
    // private List<StreamingHistory> streamingHistories = new ArrayList<>();

    // @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval =
    // true, fetch = FetchType.LAZY)
    // private List<Like> likes = new ArrayList<>();

    // @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval =
    // true, fetch = FetchType.LAZY)
    // private List<PlaylistTrack> playlistTracks = new ArrayList<>();

    @Builder
    public Track(String title, Boolean hasLyrics, String trackUrl, String trackImage, String thumbnailImage, User user,
            Genre genre) {
        this.title = title;
        this.hasLyrics = hasLyrics;
        this.trackUrl = trackUrl;
        this.trackImage = trackImage;
        this.thumbnailImage = thumbnailImage;
        this.user = user;
        this.genre = genre;
    }
}
