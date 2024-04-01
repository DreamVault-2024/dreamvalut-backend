package com.example.dreamvalutbackend.global.aws;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.dreamvalutbackend.config.aws.S3ClientProperties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.StorageClass;

@Component
@RequiredArgsConstructor
public class S3FileHandler {

    private final S3Client s3Client;
    private final S3ClientProperties s3ClientProperties;

    public String uploadFile(MultipartFile file) throws IOException {
        // S3에 파일 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3ClientProperties.getS3().getBucket())
                .key(file.getOriginalFilename())
                .contentLength(file.getSize())
                .contentType(file.getContentType())
                .storageClass(StorageClass.STANDARD)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getInputStream().readAllBytes()));

        // 업로드한 파일 URL 반환
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(s3ClientProperties.getS3().getBucket())
                .key(file.getOriginalFilename())
                .build();

        return s3Client.utilities().getUrl(getUrlRequest).toExternalForm();
    }

    public void deleteFile(String fileName) {
        // S3에 업로드한 파일 삭제
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3ClientProperties.getS3().getBucket())
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}
