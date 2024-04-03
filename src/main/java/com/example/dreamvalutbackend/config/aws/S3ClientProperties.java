package com.example.dreamvalutbackend.config.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws")
public class S3ClientProperties {

    private Credentials credentials;
    private S3 s3;

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class S3 {
        private String region;
        private String bucket;
    }
}
