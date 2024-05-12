package com.example.dreamvalutbackend.config.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {

    private String serverUrl;
    private String apiKey;
}
