package com.mcp.factorialmcpserver.integration;

import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "OAUTH2_APPLICATION_ID=dummy-id",
        "OAUTH2_APPLICATION_SECRET=dummy-secret",
        "oauth.server.host=localhost",
        "oauth.server.port=7000",
        "oauth.server.path=/oauth2-callback"
})
class HttpIntegrationTest {

    @MockitoBean
    private AuthManager authManager;

    @Test
    void itShouldHandleOAuthCallback() throws Exception {
        int port = 7000;
        String code = "test-code";
        String state = "test-state";
        String url = "http://localhost:" + port + "/oauth2-callback?code=" + code + "&state=" + state;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("✅ Auth completed");
        assertThat(response.headers().firstValue("Content-Type")).isPresent().get().asString().contains("text/html");

        verify(authManager).handleCallback(code, state);
    }

}
