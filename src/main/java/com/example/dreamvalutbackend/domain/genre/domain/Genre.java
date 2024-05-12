package com.example.dreamvalutbackend.domain.genre.domain;

import java.util.ArrayList;
import java.util.List;

import com.example.dreamvalutbackend.domain.common.BaseTimeEntity;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.user.domain.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "genres")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Genre extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String genreName;

    @Column(nullable = false)
    private String genreImage;

    // @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<Track> tracks = new ArrayList<>();

    @Builder
    public Genre(String genreName, String genreImage) {
        this.genreName = genreName;
        this.genreImage = genreImage;
    }
}
