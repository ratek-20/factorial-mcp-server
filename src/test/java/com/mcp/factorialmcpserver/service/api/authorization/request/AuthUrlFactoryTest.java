package com.mcp.factorialmcpserver.service.api.authorization.request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthUrlFactoryTest {

    private AuthUrlFactory authUrlFactory;
    private final String redirectUri = "http://localhost:8080/callback";
    private final String hostname = "api.factorialhr.com";
    private final String oauth2ApplicationId = "test-client-id";

    @BeforeEach
    void setUp() {
        authUrlFactory = new AuthUrlFactory(redirectUri, hostname, oauth2ApplicationId);
    }

    @Test
    void shouldCreateCorrectAuthUrl() {
        String state = "test-state";

        URI uri = authUrlFactory.create(state);

        assertEquals("https", uri.getScheme());
        assertEquals(hostname, uri.getHost());
        assertEquals("/oauth/authorize", uri.getPath());

        String expectedQuery = "client_id=test-client-id&redirect_uri=http://localhost:8080/callback&response_type=code&state=test-state";
        assertEquals(expectedQuery, uri.getQuery());
    }

}