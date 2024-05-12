package com.example.dreamvalutbackend.domain.like.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dreamvalutbackend.domain.like.controller.request.LikeCreateRequestDto;
import com.example.dreamvalutbackend.domain.like.controller.request.LikeDeleteRequestDto;
import com.example.dreamvalutbackend.domain.like.controller.response.LikeResponseDto;
import com.example.dreamvalutbackend.domain.like.domain.Like;
import com.example.dreamvalutbackend.domain.like.repository.LikeRepository;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackThumbnailImageResponseDto;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
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
	private final TrackDetailRepository trackDetailRepository;

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

	@Transactional(readOnly = true)
	public TrackThumbnailImageResponseDto getUserLikedTrackThumbnails(Long userId) {
		List<Track> tracks = likeRepository.findTracksByUserId(userId, PageRequest.of(0, 3));
		List<String> thumbnails = tracks.stream()
			.map(Track::getThumbnailImage)
			.collect(Collectors.toList());
		return TrackThumbnailImageResponseDto.toDto(thumbnails);
	}

	@Transactional(readOnly = true)
	public Page<TrackResponseDto> getUserLikedTracks(Long userId, Pageable pageable) {
		Page<Track> tracks = likeRepository.findTopLikedTracksByUserId(userId, pageable);
		return tracks.map(track -> {
			TrackDetail trackDetail = trackDetailRepository.findById(track.getId())
				.orElseThrow(() -> new IllegalArgumentException("Track detail not found"));
			Long likes = likeRepository.countByTrackId(track.getId());
			Boolean likesFlag = likeRepository.existsByUserIdAndTrackId(userId, track.getId());
			return TrackResponseDto.toDto(track, trackDetail, likes, likesFlag);
		});
	}




}
