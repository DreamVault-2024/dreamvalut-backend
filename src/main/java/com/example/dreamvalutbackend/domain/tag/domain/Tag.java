package com.example.dreamvalutbackend.domain.tag.domain;

import com.example.dreamvalutbackend.domain.common.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String tagName;

    @Column(nullable = false)
    private String tagImage;

    // @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<TrackTag> trackTags = new ArrayList<>();

    public Tag(String tagName, String tagImage) {
        this.tagName = tagName;
        this.tagImage = tagImage;
    }
}
