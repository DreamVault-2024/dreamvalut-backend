package com.example.dreamvalutbackend.config.elasticsearch;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

    private final ElasticsearchProperties elasticsearchProperties;
    private final ObjectMapper objectMapper;

    @Bean("elasticsearchClient")
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient
                .builder(HttpHost.create(elasticsearchProperties.getServerUrl()))
                .setDefaultHeaders(new Header[] {
                        new BasicHeader("Authorization", "ApiKey " + elasticsearchProperties.getApiKey())
                })
                .build();

        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));

        return new ElasticsearchClient(transport);
    }
}
