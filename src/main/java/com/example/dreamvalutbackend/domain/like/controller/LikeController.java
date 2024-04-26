package com.example.dreamvalutbackend.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dreamvalutbackend.domain.like.controller.request.LikeCreateRequestDto;
import com.example.dreamvalutbackend.domain.like.controller.request.LikeDeleteRequestDto;
import com.example.dreamvalutbackend.domain.like.controller.response.LikeResponseDto;
import com.example.dreamvalutbackend.domain.like.service.LikeService;
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






}
