package com.example.dreamvalutbackend.domain.like.domain;

import com.example.dreamvalutbackend.domain.common.BaseTimeEntity;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.user.domain.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private Track track;

    @Builder
    public Like(User user, Track track) {
        this.user = user;
        this.track = track;
    }
}
