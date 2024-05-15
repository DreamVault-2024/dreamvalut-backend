package com.example.dreamvalutbackend.domain.user.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.user.controller.request.UserGenreCreateRequestDto;
import com.example.dreamvalutbackend.domain.user.controller.request.UserPreferenceUpdateRequest;
import com.example.dreamvalutbackend.domain.user.controller.response.UserGenreResponseDto;
import com.example.dreamvalutbackend.domain.user.controller.response.UserInfoResponseDto;
import com.example.dreamvalutbackend.domain.user.domain.UserGenre;
import com.example.dreamvalutbackend.domain.user.repository.UserGenreRepository;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final UserGenreRepository userGenreRepository;
	private final GenreRepository genreRepository;

	@Transactional
	public void addGenre(Long userId, UserGenreCreateRequestDto genreCreateRequestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("userId가 존재하지 않습니다."));
		userGenreRepository.deleteByUser_UserId(userId);

		genreCreateRequestDto.getGenreIds().forEach(genreId -> {
			Genre genre = genreRepository.findById(genreId)
				.orElseThrow(() -> new RuntimeException("genreId가 존재하지 않습니다."));

			UserGenre userGenre = new UserGenre(user, genre);
			userGenreRepository.save(userGenre);
		});
	}

	@Transactional(readOnly = true)
	public Page<UserGenreResponseDto> getUserGenre(Long userId, Pageable pageable) {
		Page<Genre> genresPage = genreRepository.findAll(pageable);
		Set<Long> selectedGenreIds = userGenreRepository.findByUser_UserId(userId).stream()
			.map(userGenre -> userGenre.getGenre().getId())
			.collect(Collectors.toSet());

		return genresPage.map(genre -> UserGenreResponseDto.toDto(genre, selectedGenreIds.contains(genre.getId())));
	}

	@Transactional(readOnly = true)
	public UserInfoResponseDto getUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("userId가 존재하지 않습니다: " + userId));

		return UserInfoResponseDto.toDto(user);
	}

	@Transactional
	public void updateUserPreferences(Long userId, UserPreferenceUpdateRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("userId가 존재하지 않습니다: " + userId));

		user.updateDisplayName(request.getDisplayName());
		userRepository.save(user);

		userGenreRepository.deleteByUser_UserId(userId);

		request.getGenreIds().forEach(genreId -> {
			Genre genre = genreRepository.findById(genreId)
				.orElseThrow(() -> new RuntimeException("genreId가 존재하지 않습니다."));

			UserGenre userGenre = new UserGenre(user, genre);
			userGenreRepository.save(userGenre);
		});
	}



}
