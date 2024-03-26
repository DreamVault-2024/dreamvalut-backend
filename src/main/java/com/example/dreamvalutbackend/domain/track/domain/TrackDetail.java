package com.example.dreamvalutbackend.domain.track.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "track_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackDetail {

    @Id
    private Long id;

    @Column(nullable = false)
    private String prompt;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Track 테이블의 id를 TrackDetail 기본 키와 매핑
    @JoinColumn(name = "track_detail_id") // id 컬럼명을 track_detail_id로 변명
    private Track track;
}
