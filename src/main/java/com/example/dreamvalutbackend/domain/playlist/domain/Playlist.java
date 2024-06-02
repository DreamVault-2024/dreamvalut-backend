package com.example.dreamvalutbackend.domain.playlist.domain;

import com.example.dreamvalutbackend.domain.common.BaseTimeEntity;
import com.example.dreamvalutbackend.domain.user.domain.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "playlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlist extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String playlistName;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private Boolean isCurated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval =
    // true, fetch = FetchType.LAZY)
    // private List<PlaylistTrack> playlistTracks = new ArrayList<>();

    @Builder
    public Playlist(String playlistName, Boolean isPublic, Boolean isCurated, User user) {
        this.playlistName = playlistName;
        this.isPublic = isPublic;
        this.isCurated = isCurated;
        this.user = user;
    }

    public void updatePlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
}
