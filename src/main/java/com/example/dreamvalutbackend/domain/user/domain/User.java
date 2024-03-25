package com.example.dreamvalutbackend.domain.user.domain;

import com.example.dreamvalutbackend.domain.common.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true, length = 50)
    private String displayName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String profileImage;

    @Builder
    public User(String name, String displayName, String email, String profileImage) {
        this.name = name;
        this.displayName = displayName;
        this.email = email;
        this.profileImage = profileImage;
    }

    public User update(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;

        return this;
    }
}
