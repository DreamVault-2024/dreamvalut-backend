package com.example.dreamvalutbackend.domain.track.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;
import com.example.dreamvalutbackend.domain.genre.repository.GenreRepository;
import com.example.dreamvalutbackend.domain.like.repository.LikeRepository;
import com.example.dreamvalutbackend.domain.tag.service.TagService;
import com.example.dreamvalutbackend.domain.track.controller.request.TrackUploadRequestDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackOverviewResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackRankResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackUploadResponseDto;
import com.example.dreamvalutbackend.domain.track.domain.StreamingHistory;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;
import com.example.dreamvalutbackend.domain.track.repository.StreamingHistoryRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackDetailRepository;
import com.example.dreamvalutbackend.domain.track.repository.TrackRepository;
import com.example.dreamvalutbackend.domain.user.domain.User;
import com.example.dreamvalutbackend.domain.user.repository.UserRepository;
import com.example.dreamvalutbackend.global.utils.audio.AudioDataParser;
import com.example.dreamvalutbackend.global.utils.uploader.AudioUploader;
import com.example.dreamvalutbackend.global.utils.uploader.ImageUploader;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrackService {

	private final TrackRepository trackRepository;
	private final TrackDetailRepository trackDetailRepository;
	private final UserRepository userRepository;
	private final GenreRepository genreRepository;
	private final TagService tagService;
	private final StreamingHistoryRepository streamingHistoryRepository;
	private final LikeRepository likeRepository;

	private final ImageUploader imageUploader;
	private final AudioUploader audioUploader;

	@Transactional
	public TrackUploadResponseDto uploadTrack(TrackUploadRequestDto trackUploadRequestDto,
			MultipartFile trackImage, MultipartFile trackAudio, Long userId) throws IOException {

		// 이미지와 썸네일 이미지, 오디오 파일을 S3에 업로드
		String trackImageUrl = imageUploader.uploadTrackImage(trackImage, trackUploadRequestDto.getTitle());
		String thumbnailImageUrl = imageUploader.uploadThumbnailImage(trackImage, trackUploadRequestDto.getTitle());
		String trackAudioUrl = audioUploader.uploadTrackAudio(trackAudio, trackUploadRequestDto.getTitle());

		// 오디오 파일로부터 음악 길이 추출
		int duration = AudioDataParser.extractDurationInSeconds(trackAudio);

		// ID로 User 가져오가
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

		// ID로 Genre 가져오기
		Genre genre = genreRepository.findById(trackUploadRequestDto.getGenreId()).orElseThrow(
				() -> new EntityNotFoundException("Genre not found with id: " + trackUploadRequestDto.getGenreId()));

		// Track 객체 생성 및 저장
		Track track = Track.builder()
				.title(trackUploadRequestDto.getTitle())
				.duration(duration)
				.hasLyrics(trackUploadRequestDto.getHasLyrics())
				.trackUrl(trackAudioUrl)
				.trackImage(trackImageUrl)
				.thumbnailImage(thumbnailImageUrl)
				.user(user)
				.genre(genre)
				.build();
		Track savedTrack = trackRepository.save(track);

		// Track과 Tag 연결
		tagService.associateTrackWithTags(savedTrack, trackUploadRequestDto.getTags());

		// TrackDetail 객체 생성 및 저장
		TrackDetail trackDetail = TrackDetail.builder()
				.prompt(trackUploadRequestDto.getPrompt())
				.track(savedTrack)
				.build();
		trackDetailRepository.save(trackDetail);

		return TrackUploadResponseDto.toDto(savedTrack);
	}

	@Transactional(readOnly = true)
	public TrackResponseDto getTrack(Long trackId, Long userId) {

		// ID로 Track과 TrackDetail 가져오기
		Track track = trackRepository.findById(trackId)
				.orElseThrow(() -> new EntityNotFoundException("Track not found with id: " + trackId));
		TrackDetail trackDetail = trackDetailRepository.findById(trackId)
				.orElseThrow(() -> new EntityNotFoundException("TrackDetail not found with trackId: " + trackId));

		// Track의 좋아요 수와 좋아요 여부 가져오기
		Long likes = likeRepository.countByTrackId(trackId);
		Boolean likesFlag = likeRepository.existsByUserIdAndTrackId(userId, trackId);

		// TrackResponseDto로 변환하여 반환
		return TrackResponseDto.toDto(track, trackDetail, likes, likesFlag);
	}

	@Transactional
	public void recordStreamEvent(Long trackId, Long userId) {

		// ID로 User 가져오기
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

		// ID로 Track 가져오기
		Track track = trackRepository.findById(trackId)
				.orElseThrow(() -> new EntityNotFoundException("Track not found with id: " + trackId));

		// StreamingHistory 객체 생성 및 저장
		StreamingHistory streamingHistory = StreamingHistory.builder()
				.user(user)
				.track(track)
				.build();
		streamingHistoryRepository.save(streamingHistory);
	}

	@Transactional
	public Page<TrackResponseDto> getUserResentTrack(Long userId, Pageable pageable) {
		Page<Track> tracks = streamingHistoryRepository.findDistinctAndRecentTracksByUserId(userId, pageable);
		return tracks.map(track -> {
			TrackDetail trackDetail = trackDetailRepository.findById(track.getId())
				.orElseThrow(() -> new IllegalArgumentException("Track detail not found"));
			Long likes = likeRepository.countByTrackId(track.getId());
			Boolean likesFlag = likeRepository.existsByUserIdAndTrackId(userId, track.getId());
			return TrackResponseDto.toDto(track, trackDetail, likes, likesFlag);
		});
	}

	public Page<TrackRankResponseDto> getPopularTracks(Pageable pageable) {
			Page<Track> popularTracks = streamingHistoryRepository.findPopularTracks(pageable);
			List<TrackRankResponseDto> dtos = IntStream.range(0, popularTracks.getContent().size())
				.mapToObj(i -> TrackRankResponseDto.toDto(popularTracks.getContent().get(i), pageable.getPageNumber() * pageable.getPageSize() + i + 1))
				.collect(Collectors.toList());
			return new PageImpl<>(dtos, pageable, popularTracks.getTotalElements());
		}
}
