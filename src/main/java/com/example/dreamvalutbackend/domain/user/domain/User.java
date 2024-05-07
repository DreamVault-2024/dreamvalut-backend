package com.example.dreamvalutbackend.domain.user.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



import com.example.dreamvalutbackend.domain.common.BaseTimeEntity;
import com.example.dreamvalutbackend.domain.genre.domain.Genre;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, length = 50)
    private String displayName;

    @Column(nullable = false, unique = false)
    private String userEmail;

    @Column(nullable = false)
    private String profileImage;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SocialType type;

    private String socialId;

    @OneToMany(mappedBy = "user")
    private List<UserGenre> userGenres = new ArrayList<>();


    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<StreamingHistory> streamingHistories = new ArrayList<>();

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<LikeCache> likes = new ArrayList<>();

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<Playlist> playlists = new ArrayList<>();

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<UserGenre> userGenres = new ArrayList<>();
}
