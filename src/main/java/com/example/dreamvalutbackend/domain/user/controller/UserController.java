package com.example.dreamvalutbackend.domain.user.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dreamvalutbackend.domain.user.controller.request.UserGenreCreateRequestDto;
import com.example.dreamvalutbackend.domain.user.controller.request.UserPreferenceUpdateRequest;
import com.example.dreamvalutbackend.domain.user.controller.response.UserGenreResponseDto;
import com.example.dreamvalutbackend.domain.user.controller.response.UserInfoResponseDto;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;
import com.example.dreamvalutbackend.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	@PostMapping("/users/preference")
	public ResponseEntity<Void> createGenre( @RequestBody UserGenreCreateRequestDto genreCreateRequestDto,
		@AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {
		Long userId = userDetailPrincipal.getUserId();
		userService.addGenre(userId, genreCreateRequestDto);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/users/preference")
	public ResponseEntity<Page<UserGenreResponseDto>> getUserPreferences(
		@AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal,
		@PageableDefault(page = 0, size = 8) Pageable pageable) {
		Long userId = userDetailPrincipal.getUserId();
		Page<UserGenreResponseDto> userGenres = userService.getUserGenre(userId, pageable);

		return ResponseEntity.ok(userGenres);
	}

	@GetMapping("/users")
	public ResponseEntity<UserInfoResponseDto> getUserInfo(
		@AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {
		Long userId = userDetailPrincipal.getUserId();
		UserInfoResponseDto userInfo = userService.getUser(userId);

		return ResponseEntity.ok(userInfo);
	}

	@PatchMapping("/users")
	public ResponseEntity<Void> updateUserPreferences(
		@AuthenticationPrincipal(expression = "id") Long userId,
		@RequestBody UserPreferenceUpdateRequest request) {
		userService.updateUserPreferences(userId, request);
		return ResponseEntity.ok().build();
	}

}
