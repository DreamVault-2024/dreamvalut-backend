package com.example.dreamvalutbackend.global.utils.uploader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.dreamvalutbackend.global.aws.S3FileHandler;
import com.example.dreamvalutbackend.global.utils.multipart.ThumbnailImageMultipartFile;
import com.example.dreamvalutbackend.global.utils.multipart.TrackImageMultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageUploader {

    private final S3FileHandler s3FileHandler;

    @Value("${aws.s3.default-image}")
    private String defaultImageUrl;

    public String uploadTrackImage(MultipartFile trackImage, String title) {
        try {
            TrackImageMultipartFile trackImageMultipartFile = new TrackImageMultipartFile(trackImage, title);
            String trackImageUrl = s3FileHandler.uploadFile(trackImageMultipartFile);
            return trackImageUrl;
        } catch (Exception e) {
            log.error("Failed to upload track image: {}, use default image instead", e.getMessage());
            return defaultImageUrl;
        }
    }

    public String uploadThumbnailImage(MultipartFile thumbnailImage, String title) {
        try {
            ThumbnailImageMultipartFile thumbnailImageMultipartFile = new ThumbnailImageMultipartFile(thumbnailImage,
                    title);
            String thumbnailImageUrl = s3FileHandler.uploadFile(thumbnailImageMultipartFile);
            return thumbnailImageUrl;
        } catch (Exception e) {
            log.error("Failed to upload thumbnail image: {}, use default image instead", e.getMessage());
            return defaultImageUrl;
        }
    }
}