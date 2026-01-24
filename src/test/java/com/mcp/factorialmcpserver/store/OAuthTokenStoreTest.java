package com.mcp.factorialmcpserver.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.factorialmcpserver.model.authorization.OAuthToken;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class OAuthTokenStoreTest {

    @Mock
    private ObjectMapper objectMapper;

    private OAuthTokenStore oauthTokenStore;

    private MockedStatic<Files> filesMockedStatic;

    private static final Path TOKEN_FILE_PATH = Path.of("/app/data", "oauth_token.json");

    @BeforeEach
    void setUp() {
        // Initialize SLF4J before mocking Files to avoid NoClassDefFoundError/InitializationError
        LoggerFactory.getLogger(OAuthTokenStore.class);
        
        filesMockedStatic = mockStatic(Files.class);
        filesMockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(true);
        oauthTokenStore = new OAuthTokenStore(objectMapper);
    }

    @AfterEach
    void tearDown() {
        if (filesMockedStatic != null && !filesMockedStatic.isClosed()) {
            filesMockedStatic.close();
        }
    }

    @Test
    void itShouldReturnEmptyOptionalWhenFileDoesNotExist() {
        filesMockedStatic.when(() -> Files.exists(TOKEN_FILE_PATH)).thenReturn(false);

        Optional<OAuthToken> result = oauthTokenStore.load();

        assertTrue(result.isEmpty());
    }

    @Test
    void itShouldReturnTokenWhenFileExistsAndIsValid() throws IOException {
        OAuthToken token = Instancio.create(OAuthToken.class);
        byte[] tokenBytes = "token-bytes".getBytes();
        filesMockedStatic.when(() -> Files.exists(TOKEN_FILE_PATH)).thenReturn(true);
        filesMockedStatic.when(() -> Files.readAllBytes(TOKEN_FILE_PATH)).thenReturn(tokenBytes);
        when(objectMapper.readValue(tokenBytes, OAuthToken.class)).thenReturn(token);

        Optional<OAuthToken> result = oauthTokenStore.load();

        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    void itShouldReturnEmptyOptionalWhenIOExceptionOccursDuringLoad() throws IOException {
        filesMockedStatic.when(() -> Files.exists(TOKEN_FILE_PATH)).thenReturn(true);
        filesMockedStatic.when(() -> Files.readAllBytes(TOKEN_FILE_PATH)).thenThrow(new IOException("Read error"));

        Optional<OAuthToken> result = oauthTokenStore.load();

        assertTrue(result.isEmpty());
    }

    @Test
    void itShouldSaveTokenSuccessfully() throws IOException {
        OAuthToken token = Instancio.create(OAuthToken.class);
        byte[] tokenBytes = "token-bytes".getBytes();
        when(objectMapper.writeValueAsBytes(token)).thenReturn(tokenBytes);

        oauthTokenStore.save(token);

        filesMockedStatic.verify(() -> Files.write(TOKEN_FILE_PATH, tokenBytes));
    }

    @Test
    void itShouldHandleIOExceptionDuringSave() throws IOException {
        OAuthToken token = Instancio.create(OAuthToken.class);
        byte[] tokenBytes = "token-bytes".getBytes();
        when(objectMapper.writeValueAsBytes(token)).thenReturn(tokenBytes);
        filesMockedStatic.when(() -> Files.write(TOKEN_FILE_PATH, tokenBytes)).thenThrow(new IOException("Write error"));

        oauthTokenStore.save(token);

        filesMockedStatic.verify(() -> Files.write(TOKEN_FILE_PATH, tokenBytes));
    }

    @Test
    void itShouldCreateDirectoryWhenItDoesNotExist() {
        // This is actually called in the constructor, which is called in @BeforeEach.
        // To test it properly, we need to mock Files before the constructor call.
        filesMockedStatic.close(); // Close the one from setUp
        
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path parentDir = TOKEN_FILE_PATH.getParent();
            filesMock.when(() -> Files.exists(parentDir)).thenReturn(false);
            
            new OAuthTokenStore(objectMapper);
            
            filesMock.verify(() -> Files.createDirectories(parentDir));
        }
    }
}
