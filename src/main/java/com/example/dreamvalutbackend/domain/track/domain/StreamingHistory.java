package com.example.dreamvalutbackend.domain.track.domain;

import com.example.dreamvalutbackend.domain.common.BaseTimeEntity;
import com.example.dreamvalutbackend.domain.user.domain.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "streaming_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StreamingHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "streaming_history_id")
    private Long id;

    // 스트리밍 시간(초) - 측정 어려울 시, 해당 필드 사용 안 할 수 있음
    @Column(nullable = false)
    private Integer streamingDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private Track track;
}
