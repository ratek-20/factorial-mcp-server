package com.mcp.factorialmcpserver.service.api.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpClientConfigTest {

    @InjectMocks
    private HttpClientConfig httpClientConfig;

    @Mock
    private RestClient.Builder builder;

    @Mock
    private RestClient restClient;

    @Test
    void itShouldConfigureRestClient() {
        String baseUrl = "https://api.factorialhr.com";
        String version = "/v2";

        when(builder.baseUrl("https://api.factorialhr.com/v2")).thenReturn(builder);
        when(builder.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);

        RestClient result = httpClientConfig.baseClient(builder, baseUrl, version);

        assertEquals(restClient, result);
        verify(builder).baseUrl("https://api.factorialhr.com/v2");
        verify(builder).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        verify(builder).build();
    }
}
