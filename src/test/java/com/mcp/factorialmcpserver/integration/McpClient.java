package com.mcp.factorialmcpserver.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 Mimics an MCP client like Claude or Gemini CLI
 that communicates with an MCP server using JSON-RPC 2.0 over STDIO.
 */
class McpClient implements Closeable {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BufferedWriter out;
    private final BufferedReader in;
    private final ConcurrentMap<Long, CompletableFuture<JsonNode>> pending = new ConcurrentHashMap<>();
    /*
    The reader thread constantly listens to the server's output and completes
    pending requests, simulating the asynchronous nature of JSON-RPC communication.
     */
    private final ExecutorService reader = Executors.newSingleThreadExecutor();
    private final Duration timeout;

    private volatile long idSeq = 1;

    McpClient(InputStream serverStdout, OutputStream serverStdin, Duration timeout) {
        this.in = new BufferedReader(new InputStreamReader(serverStdout));
        this.out = new BufferedWriter(new OutputStreamWriter(serverStdin));
        this.timeout = timeout;

        reader.submit(() -> {
            String line;
            while ((line = in.readLine()) != null) {
                JsonNode msg = objectMapper.readTree(line);
                if (msg.has("id")) {
                    long id = msg.get("id").asLong();
                    CompletableFuture<JsonNode> responseFuture = pending.remove(id);
                    if (responseFuture != null) {
                        responseFuture.complete(msg);
                    }
                }
            }
            return null;
        });
    }

    // Implements the mandatory MCP handshake: initialize -> response -> initialized notification.
    void initialize() throws Exception {
        JsonNode resp = request("initialize", Map.of(
                "protocolVersion", "2024-11-05",
                "capabilities", Map.of(),
                "clientInfo", Map.of("name", "test-client", "version", "0.0.0")
        ));
        if (resp.has("error")) {
            throw new RuntimeException(resp.get("error").toString());
        }
        notify("notifications/initialized", Map.of());
    }

    JsonNode listTools() throws Exception {
        return request("tools/list", Map.of());
    }

    JsonNode callTool(String toolName) throws Exception {
        return request("tools/call", Map.of("name", toolName, "arguments", Map.of()));
    }

    private void notify(String method, Object params) throws Exception {
        Map<String, Object> req = Map.of(
                "jsonrpc", "2.0",
                "method", method,
                "params", params
        );

        out.write(objectMapper.writeValueAsString(req));
        out.write("\n");
        out.flush();
    }

    private JsonNode request(String method, Object params) throws Exception {
        long id = idSeq++;
        CompletableFuture<JsonNode> responseFuture = new CompletableFuture<>();
        pending.put(id, responseFuture);

        Map<String, Object> req = Map.of(
                "jsonrpc", "2.0",
                "id", id,
                "method", method,
                "params", params
        );

        out.write(objectMapper.writeValueAsString(req));
        out.write("\n");
        out.flush();

        return responseFuture.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        reader.shutdownNow();
    }
}

