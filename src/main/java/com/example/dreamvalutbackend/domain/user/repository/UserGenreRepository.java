package com.example.dreamvalutbackend.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.user.domain.UserGenre;

public interface UserGenreRepository extends JpaRepository<UserGenre, Long> {
	List<UserGenre> findByUserId(Long userId);

	void deleteByUserId(Long userId);

	boolean existsByUserId(Long userId);

	// Optional<UserGenre> findByUserIdAndGenreId(Long userId, Long genreId);
}
