package com.example.dreamvalutbackend.domain.track.controller;

import java.io.IOException;
import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.dreamvalutbackend.domain.track.controller.request.TrackUploadRequestDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackOverviewResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackRankResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackUploadResponseDto;
import com.example.dreamvalutbackend.domain.track.service.TrackService;
import com.example.dreamvalutbackend.domain.track.validation.annotation.ValidTrackAudio;
import com.example.dreamvalutbackend.domain.track.validation.annotation.ValidTrackImage;
import com.example.dreamvalutbackend.domain.user.domain.UserDetailPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tracks")
public class TrackController {

	private final TrackService trackService;

	@PostMapping
	@Operation(summary = "특정 곡 등록하기", description = "나만의 음악 등록 페이지에서 음악 업로드하기")
	public ResponseEntity<TrackUploadResponseDto> uploadTrack(
			@Valid @RequestPart("track_info") TrackUploadRequestDto trackUploadRequestDto,
			@ValidTrackImage @RequestPart("track_image") MultipartFile trackImage,
			@ValidTrackAudio @RequestPart("track_audio") MultipartFile trackAudio,
			@AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) throws IOException {

		// TrackService를 통해 Track 엔티티를 생성하고 저장
		TrackUploadResponseDto trackUploadResponseDto = trackService.uploadTrack(trackUploadRequestDto, trackImage,
				trackAudio, userDetailPrincipal.getUserId());

		// HTTP 201 Created 상태 코드와 함께 생성된 리소스의 URI를 반환
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{track_id}")
				.buildAndExpand(trackUploadResponseDto.getTrackId())
				.toUri();

		return ResponseEntity.created(location)
				.body(trackUploadResponseDto);
	}

	@GetMapping("/{track_id}")
	@Operation(summary = "특정 곡 정보 가져오기", description = "특정 곡의 음악 상세정보 가져오기 및 스트리밍하기")
	public ResponseEntity<TrackResponseDto> getTrack(@PathVariable("track_id") Long trackId,
			@AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

		return ResponseEntity.ok(trackService.getTrack(trackId, userDetailPrincipal.getUserId()));
	}

	@PostMapping("/{track_id}/stream_events")
	@Operation(summary = "특정 곡 재생 이벤트 기록하기", description = "곡 재생시, 해당 재생 시간 기록하기")
	public ResponseEntity<Void> recordStreamEvent(@PathVariable("track_id") Long trackId,
			@AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal) {

		trackService.recordStreamEvent(trackId, userDetailPrincipal.getUserId());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/users/played")
	public ResponseEntity<Page<TrackResponseDto>> getRecentTracks(
		@AuthenticationPrincipal UserDetailPrincipal userDetailPrincipal,
		@PageableDefault(size = 12, sort = "createdAt") Pageable pageable) {

		Long userId = userDetailPrincipal.getUserId();
		Page<TrackResponseDto> recentTracks = trackService.getUserResentTrack(userId, pageable);
		return ResponseEntity.ok(recentTracks);
	}

	@GetMapping("/charts")
	public ResponseEntity<Page<TrackRankResponseDto>> getPopularTracks(@PageableDefault(size = 12, sort = "createdAt")Pageable pageable) {
		Page<TrackRankResponseDto> tracks = trackService.getPopularTracks(pageable);
		return ResponseEntity.ok(tracks);
	}
}
