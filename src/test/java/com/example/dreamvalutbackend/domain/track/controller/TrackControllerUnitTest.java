package com.example.dreamvalutbackend.domain.track.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.example.dreamvalutbackend.domain.track.controller.request.TrackUploadRequestDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackResponseDto;
import com.example.dreamvalutbackend.domain.track.controller.response.TrackUploadResponseDto;
import com.example.dreamvalutbackend.domain.track.service.TrackService;

public class TrackControllerUnitTest {

	// HTTP 요청 처리를 위한 MockMvc 객체
	private MockMvc mockMvc;

	// TrackService 객체를 Mock으로 주입
	@Mock
	private TrackService trackService;

	// TrackController 객체를 생성하면서 TrackService 객체를 주입
	@InjectMocks
	private TrackController trackController;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(trackController).build();
	}

	@Test
	@DisplayName("POST /tracks - Unit Success")
	public void uploadTrackSuccess() throws Exception {
		/* Given */

		// HTTP Post 요청 시, track_info 파트에 전달할 JSON 데이터
		String trackInfoJson = "{\n" +
				"  \"title\": \"TestTitle\",\n" +
				"  \"prompt\": \"TestPrompt\",\n" +
				"  \"hasLyrics\": true,\n" +
				"  \"tags\": [\"TestTag1\", \"TestTag2\"],\n" +
				"  \"genreId\": 1\n" +
				"}";

		// Mock MultipartFile 객체 생성 (track_info, track_image, track_audio)
		MockMultipartFile trackInfo = new MockMultipartFile("track_info", "test.json", "application/json",
				trackInfoJson.getBytes());
		MockMultipartFile trackImage = new MockMultipartFile("track_image", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
				"image".getBytes());
		MockMultipartFile trackAudio = new MockMultipartFile("track_audio", "test.mp3", "audio/mpeg",
				"audio".getBytes());

		// TrackService.uploadTrack() 메소드가 리턴할 TrackResponseDto 객체 생성
		TrackUploadResponseDto trackResponseDto = TrackUploadResponseDto.builder()
				.trackId(1L)
				.title("TestTitle")
				.duration(120)
				.hasLyrics(true)
				.trackUrl("https://example-bucket.s3.amazonaws.com/audio/testTrackUrl.wav")
				.trackImage("https://example-bucket.s3.amazonaws.com/image/testTrackImage.jpeg")
				.thumbnailImage("https://example-bucket.s3.amazonaws.com/image/testThumbnailImage.jpeg")
				.build();

		// TrackService.uploadTrack() 메소드가 호출될 때 리턴할 TrackResponseDto 객체 설정
		given(trackService.uploadTrack(any(TrackUploadRequestDto.class), any(MultipartFile.class),
				any(MultipartFile.class))).willReturn(trackResponseDto);

		/* When & Then */
		mockMvc.perform(multipart("/tracks")
				.file(trackInfo)
				.file(trackImage)
				.file(trackAudio)
				.contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "http://localhost/tracks/1"))
				.andExpect(jsonPath("$.trackId").value(1L))
				.andExpect(jsonPath("$.title").value("TestTitle"))
				.andExpect(jsonPath("$.duration").value(120))
				.andExpect(jsonPath("$.hasLyrics").value(true))
				.andExpect(
						jsonPath("$.trackUrl").value("https://example-bucket.s3.amazonaws.com/audio/testTrackUrl.wav"))
				.andExpect(jsonPath("$.trackImage")
						.value("https://example-bucket.s3.amazonaws.com/image/testTrackImage.jpeg"))
				.andExpect(jsonPath("$.thumbnailImage")
						.value("https://example-bucket.s3.amazonaws.com/image/testThumbnailImage.jpeg"));
	}

	@Test
	@DisplayName("GET /tracks/{track_id} - Unit Success")
	void getTrackSuccess() throws Exception {
		/* Given */

		// 요청할 Track ID:
		Long trackId = 1L;

		// TrackService.getTrack() 메소드가 리턴할 TrackResponseDto 객체 생성
		TrackResponseDto trackResponseDto = TrackResponseDto.builder()
				.trackId(1L)
				.title("TestTitle")
				.uploaderName("TestUploader")
				.duration(120)
				.hasLyrics(true)
				.trackUrl("https://example-bucket.s3.amazonaws.com/audio/testTrackUrl.wav")
				.trackImage("https://example-bucket.s3.amazonaws.com/image/testTrackImage.jpeg")
				.thumbnailImage("https://example-bucket.s3.amazonaws.com/image/testThumbnailImage.jpeg")
				.prompt("TestPrompt")
				.build();

		// TrackService.getTrack() 메소드가 호출될 때 리턴할 TrackResponseDto 객체 설정
		given(trackService.getTrack(any(Long.class),any(Long.class))).willReturn(trackResponseDto);

		/* When & Then */
		mockMvc.perform(get("/tracks/{track_id}", trackId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.trackId").value(1L))
				.andExpect(jsonPath("$.title").value("TestTitle"))
				.andExpect(jsonPath("$.uploaderName").value("TestUploader"))
				.andExpect(jsonPath("$.duration").value(120))
				.andExpect(jsonPath("$.hasLyrics").value(true))
				.andExpect(
						jsonPath("$.trackUrl").value("https://example-bucket.s3.amazonaws.com/audio/testTrackUrl.wav"))
				.andExpect(jsonPath("$.trackImage")
						.value("https://example-bucket.s3.amazonaws.com/image/testTrackImage.jpeg"))
				.andExpect(jsonPath("$.thumbnailImage")
						.value("https://example-bucket.s3.amazonaws.com/image/testThumbnailImage.jpeg"))
				.andExpect(jsonPath("$.prompt").value("TestPrompt"));
	}

	@Test
	@DisplayName("POST /tracks/{track_id}/stream_events - Unit Success")
	void recordStreamEventSuccess() throws Exception {
		/* Given */

		// 요청할 Track ID:
		Long trackId = 1L;

		/* When & Then */
		mockMvc.perform(post("/tracks/{track_id}/stream_events", trackId))
				.andExpect(status().isOk());

		verify(trackService).recordStreamEvent(trackId);
	}
}
