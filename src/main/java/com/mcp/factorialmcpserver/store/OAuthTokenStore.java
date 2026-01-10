package com.mcp.factorialmcpserver.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.factorialmcpserver.model.authorization.OAuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

@Repository
public class OAuthTokenStore {

    private final ObjectMapper objectMapper;

    private static final Path TOKEN_FILE_PATH = Path.of("/app/data", "oauth_token.json");
    private static final Logger logger = LoggerFactory.getLogger(OAuthTokenStore.class);

    public OAuthTokenStore(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        Path parentDir = TOKEN_FILE_PATH.getParent();
        if (Objects.nonNull(parentDir) && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                logger.error("Could not create cache directory: {}", parentDir, e);
            }
        }
    }

    public Optional<OAuthToken> load() {
        if (!Files.exists(TOKEN_FILE_PATH)) {
            return Optional.empty();
        }
        try {
            final byte[] bytes = Files.readAllBytes(TOKEN_FILE_PATH);
            return Optional.of(objectMapper.readValue(bytes, OAuthToken.class));
        } catch (IOException e) {
            logger.error("Failed to load OAuth token from {}", TOKEN_FILE_PATH, e);
            return Optional.empty();
        }
    }

    public void save(OAuthToken token) {
        try {
            final byte[] bytes = objectMapper.writeValueAsBytes(token);
            Files.write(TOKEN_FILE_PATH, bytes);
            logger.info("OAuth token saved to {}", TOKEN_FILE_PATH);
        } catch (IOException e) {
            logger.error("Failed to save OAuth token to {}", TOKEN_FILE_PATH, e);
        }
    }

}
