package com.example.dreamvalutbackend.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.user.domain.UserGenre;

public interface UserGenreRepository extends JpaRepository<UserGenre, Long> {
	List<UserGenre> findByUser_UserId(Long userId);

	void deleteByUser_UserId(Long userId);

	boolean existsByUser_UserId(Long userId);

	// Optional<UserGenre> findByUserIdAndGenreId(Long userId, Long genreId);
}
