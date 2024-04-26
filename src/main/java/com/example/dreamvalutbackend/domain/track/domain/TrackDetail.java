package com.example.dreamvalutbackend.domain.track.domain;

import com.example.dreamvalutbackend.domain.common.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "track_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackDetail extends BaseTimeEntity {

    @Id
    private Long id;

    @Column(nullable = false, length = 1024)
    private String prompt;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "track_detail_id")
    private Track track;

    @Builder
    public TrackDetail(String prompt, Track track) {
        this.prompt = prompt;
        this.track = track;
    }
}
