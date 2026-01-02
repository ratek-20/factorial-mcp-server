package com.mcp.factorialmcpserver.service.api.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    RestClient baseClient(
            RestClient.Builder builder,
            @Value("${factorial-api.base-url}") String baseUrl,
            @Value("${factorial-api.version}") String version
    ) {
        return builder
                .baseUrl(baseUrl + version)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
