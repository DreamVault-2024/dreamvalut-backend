package com.example.dreamvalutbackend.domain.like.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dreamvalutbackend.domain.like.controller.request.LikeCreateRequestDto;
import com.example.dreamvalutbackend.domain.like.controller.request.LikeDeleteRequestDto;
import com.example.dreamvalutbackend.domain.like.controller.response.LikeResponseDto;
import com.example.dreamvalutbackend.domain.like.controller.response.LikeWithTracksOverviewResponseDto;
import com.example.dreamvalutbackend.domain.like.service.LikeService;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackThumbnailImageResponseDto;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
public class LikeController {

	private final LikeService likeService;

	@PostMapping("/tracks/{trackId}/likes")
	public ResponseEntity<LikeResponseDto> createLike(@PathVariable Long trackId, @AuthenticationPrincipal
	UserDetailPrincipal userDetailPrincipal) {
		LikeCreateRequestDto requestDto = LikeCreateRequestDto.builder()
			.userId(userDetailPrincipal.getUserId())
			.trackId(trackId)
			.build();

		LikeResponseDto responseDto = likeService.addLike(requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@DeleteMapping("/tracks/{trackId}/disLikes")
	public ResponseEntity<LikeResponseDto> deleteLike(@PathVariable Long trackId, @AuthenticationPrincipal
	UserDetailPrincipal userDetailPrincipal){
		LikeDeleteRequestDto requestDto = LikeDeleteRequestDto.builder()
			.userId(userDetailPrincipal.getUserId())
			.trackId(trackId)
			.build();

	likeService.deleteLike(requestDto);

	return ResponseEntity.noContent().build(); //반환 내용이 없어서 204
	}

	@GetMapping("/users/liked")
	public ResponseEntity<TrackThumbnailImageResponseDto> getLikedTrackThumbnails(@AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {
		TrackThumbnailImageResponseDto responseDto = likeService.getUserLikedTrackThumbnails(userDetailPrincipal.getUserId());
		return ResponseEntity.ok(responseDto);
	}

	@GetMapping("/users/liked/tracks")
	public ResponseEntity<LikeWithTracksOverviewResponseDto> getUserLikedTracks(
		@AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal,
		@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

		Long userId = userDetailPrincipal.getUserId();
		Page<TrackResponseDto> tracks = likeService.getUserLikedTracks(userId, pageable);
		LikeWithTracksOverviewResponseDto responseDto = LikeWithTracksOverviewResponseDto.toDto(tracks);
		return ResponseEntity.ok(responseDto);
	}






}
