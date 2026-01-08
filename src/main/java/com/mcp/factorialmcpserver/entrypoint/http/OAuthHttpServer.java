package com.mcp.factorialmcpserver.entrypoint.http;

import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

@Component
public class OAuthHttpServer implements SmartLifecycle {

    private final String host;
    private final String path;
    private final int port;
    private final QueryStringParser queryStringParser;
    private final AuthManager authManager;

    private volatile boolean running = false;
    private HttpServer server;

    private static final String ALLOWED_HTTP_METHOD = "GET";
    private static final String CODE_QUERY_PARAM = "code";
    private static final String STATE_QUERY_PARAM = "state";

    public OAuthHttpServer(
            @Value("${oauth.server.host}") String host, @Value("${oauth.server.path}") String path, @Value("${oauth.server.port}") int port,
            QueryStringParser queryStringParser, AuthManager authManager
    ) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.queryStringParser = queryStringParser;
        this.authManager = authManager;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }
        try {
            server = HttpServer.create(new InetSocketAddress(host, port), 0);
            server.createContext(path, this::handleCallback);
            server.setExecutor(Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable, "oauth-callback-http");
                thread.setDaemon(true);
                return thread;
            }));
            server.start();
            running = true;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start OAuth server", e);
        }
    }

    private void handleCallback(HttpExchange exchange) throws IOException {
        if (!ALLOWED_HTTP_METHOD.equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        final URI uri = exchange.getRequestURI();
        final Map<String, String> query = queryStringParser.parse(uri.getQuery());
        authManager.handleCallback(query.get(CODE_QUERY_PARAM), query.get(STATE_QUERY_PARAM));
        final String html = """
                  <html><body style="font-family: sans-serif">
                    <h2>✅ Auth completed</h2>
                    <p>You can close this window.</p>
                  </body></html>
                """;
        final byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        final Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/html; charset=utf-8");
        headers.set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    @Override
    public void stop() {
        if (Objects.nonNull(server)) {
            server.stop(0);
            server = null;
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }
}

