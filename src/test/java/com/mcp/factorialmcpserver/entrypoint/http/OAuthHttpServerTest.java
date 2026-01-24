package com.mcp.factorialmcpserver.entrypoint.http;

import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthHttpServerTest {

    private OAuthHttpServer oAuthHttpServer;

    @Mock
    private QueryStringParser queryStringParser;

    @Mock
    private AuthManager authManager;

    @Mock
    private HttpExchange exchange;

    private final String host = "localhost";
    private final String path = "/callback";
    private final int port = 8080;

    @BeforeEach
    void setUp() {
        oAuthHttpServer = new OAuthHttpServer(host, path, port, queryStringParser, authManager);
    }

    @Test
    void itShouldReturnCorrectPhase() {
        assertEquals(Integer.MIN_VALUE, oAuthHttpServer.getPhase());
    }

    @Test
    void itShouldReturnAutoStartupTrue() {
        assertTrue(oAuthHttpServer.isAutoStartup());
    }

    @Test
    void itShouldReturnRunningStatus() {
        assertFalse(oAuthHttpServer.isRunning());
    }

    @Test
    void itShouldReturnMethodNotAllowedWhenHttpMethodIsNotGet() throws Exception {
        when(exchange.getRequestMethod()).thenReturn("POST");

        invokeHandleCallback(exchange);

        verify(exchange).sendResponseHeaders(405, -1);
    }

    @Test
    void itShouldHandleCallbackSuccessfullyWhenValidRequestIsProvided() throws Exception {
        String code = "auth-code-123";
        String state = "state-456";
        String query = "code=" + code + "&state=" + state;
        URI uri = URI.create("http://localhost:8080/callback?" + query);
        Headers responseHeaders = new Headers();
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(uri);
        when(queryStringParser.parse(query)).thenReturn(Map.of("code", code, "state", state));
        when(exchange.getResponseHeaders()).thenReturn(responseHeaders);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        invokeHandleCallback(exchange);

        verify(authManager).handleCallback(code, state);
        verify(exchange).sendResponseHeaders(200, responseBody.size());
        assertEquals("text/html; charset=utf-8", responseHeaders.getFirst("Content-Type"));
        assertEquals("no-store", responseHeaders.getFirst("Cache-Control"));
        assertTrue(responseBody.toString().contains("✅ Auth completed"));
    }

    private void invokeHandleCallback(HttpExchange exchange) throws Exception {
        Method method = OAuthHttpServer.class.getDeclaredMethod("handleCallback", HttpExchange.class);
        method.setAccessible(true);
        try {
            method.invoke(oAuthHttpServer, exchange);
        } catch (Exception e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            throw e;
        }
    }
}
