package com.mcp.factorialmcpserver.service.api.authorization.mapper;

import com.mcp.factorialmcpserver.model.authorization.OAuthToken;
import com.mcp.factorialmcpserver.service.api.authorization.response.OauthResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenMapperTest {

    private TokenMapper tokenMapper;

    @BeforeEach
    void setUp() {
        tokenMapper = new TokenMapper();
    }

    @Test
    void shouldMapOauthResponseToOAuthToken() {
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        long expiresIn = 3600L;
        OauthResponse oauthResponse = Instancio.of(OauthResponse.class)
                .set(field(OauthResponse::access_token), accessToken)
                .set(field(OauthResponse::refresh_token), refreshToken)
                .set(field(OauthResponse::expires_in), expiresIn)
                .create();
        Instant now = Instant.now();

        OAuthToken oauthtoken = tokenMapper.map(oauthResponse);

        assertEquals(accessToken, oauthtoken.accessTokenValue());
        assertEquals(refreshToken, oauthtoken.refreshToken());
        
        Instant expiresAt = oauthtoken.accessToken().expiresAt();
        Instant expectedExpiresAt = now.plusSeconds(expiresIn);
        assertTrue(Duration.between(expiresAt, expectedExpiresAt).getSeconds() < 2);
    }
}
