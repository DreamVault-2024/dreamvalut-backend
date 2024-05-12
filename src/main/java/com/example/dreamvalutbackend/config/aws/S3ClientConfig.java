package com.example.dreamvalutbackend.config.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {

    private final S3ClientProperties s3ClientProperties;

    @Bean("s3AsyncClient")
    public S3AsyncClient s3AsyncClient() {

        AwsCredentials credentials = AwsBasicCredentials.create(
                s3ClientProperties.getCredentials().getAccessKey(),
                s3ClientProperties.getCredentials().getSecretKey());

        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        return S3AsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(s3ClientProperties.getS3().getRegion()))
                .build();
    }

    @Bean("s3Client")
    public S3Client s3Client() {

        AwsCredentials credentials = AwsBasicCredentials.create(
                s3ClientProperties.getCredentials().getAccessKey(),
                s3ClientProperties.getCredentials().getSecretKey());

        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        return S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(s3ClientProperties.getS3().getRegion()))
                .build();
    }
}