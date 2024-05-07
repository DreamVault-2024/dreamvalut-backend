package com.example.dreamvalutbackend.domain.user.domain;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_genres")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGenre {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "genre_id")
	private Genre genre;

	@Builder
	public UserGenre(User user, Genre genre) {
		this.user = user;
		this.genre = genre;
	}
}
