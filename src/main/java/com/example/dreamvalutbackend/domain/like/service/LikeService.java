package com.example.dreamvalutbackend.domain.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.like.controller.request.LikeCreateRequestDto;
import com.example.dreamvalutbackend.domain.like.controller.request.LikeDeleteRequestDto;
import com.example.dreamvalutbackend.domain.like.controller.response.LikeResponseDto;
import com.example.dreamvalutbackend.domain.like.domain.Like;
import com.example.dreamvalutbackend.domain.like.repository.LikeRepository;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {
	private final LikeRepository likeRepository;
	private final UserRepository userRepository;
	private final TrackRepository trackRepository;

	@Transactional
	public LikeResponseDto addLike(LikeCreateRequestDto likeCreateRequestDto) {
		User user = userRepository.findById(likeCreateRequestDto.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("userId가 존재하지 않습니다"));

		Track track = trackRepository.findById(likeCreateRequestDto.getTrackId())
			.orElseThrow(() -> new IllegalArgumentException("trackId가 존재하지 않습니다"));

		Like like = Like.builder()
			.user(user)
			.track(track)
			.build();
		likeRepository.save(like);

		return LikeResponseDto.toDto(like);
	}

	@Transactional
	public void deleteLike(LikeDeleteRequestDto likeDeleteRequestDto) {
		User user = userRepository.findById(likeDeleteRequestDto.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다: " + likeDeleteRequestDto.getUserId()));
		Track track = trackRepository.findById(likeDeleteRequestDto.getTrackId())
			.orElseThrow(() -> new IllegalArgumentException("트랙이 존재하지 않습니다 " + likeDeleteRequestDto.getTrackId()));

		Like like = likeRepository.findByUserAndTrack(user, track)
			.orElseThrow(() -> new IllegalArgumentException("좋아요가 존재하지 않습니다."));

		likeRepository.delete(like);
	}




}
