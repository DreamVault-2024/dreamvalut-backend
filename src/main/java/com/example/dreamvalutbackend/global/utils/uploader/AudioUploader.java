package com.example.dreamvalutbackend.global.utils.uploader;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.dreamvalutbackend.global.aws.S3FileHandler;
import com.example.dreamvalutbackend.global.utils.multipart.TrackAudioMultipartFile;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AudioUploader {

    private final S3FileHandler s3FileHandler;

    public String uploadTrackAudio(MultipartFile trackAudio, String title) throws IOException {
        TrackAudioMultipartFile trackAudioMultipartFile = new TrackAudioMultipartFile(trackAudio, title);
        String trackAudioUrl = s3FileHandler.uploadFile(trackAudioMultipartFile);
        return trackAudioUrl;
    }
}
