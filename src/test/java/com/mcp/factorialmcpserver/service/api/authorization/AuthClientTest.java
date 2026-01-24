package com.mcp.factorialmcpserver.service.api.authorization;

import com.mcp.factorialmcpserver.model.authorization.OAuthToken;
import com.mcp.factorialmcpserver.service.api.authorization.mapper.TokenMapper;
import com.mcp.factorialmcpserver.service.api.authorization.request.RefreshTokenRequest;
import com.mcp.factorialmcpserver.service.api.authorization.request.RequestTokenRequest;
import com.mcp.factorialmcpserver.service.api.authorization.response.OauthResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthClientTest {

    private AuthClient authClient;

    @Mock
    private RestClient baseClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private TokenMapper tokenMapper;

    private final String redirectUri = "http://localhost:8080/callback";
    private final String oauth2ApplicationId = "test-client-id";
    private final String oauth2ApplicationSecret = "test-client-secret";

    @BeforeEach
    void setUp() {
        authClient = new AuthClient(
                baseClient,
                tokenMapper,
                redirectUri,
                oauth2ApplicationId,
                oauth2ApplicationSecret
        );
    }

    @Test
    void itShouldRequestAToken() {
        String authCode = "test-auth-code";
        OauthResponse oauthResponse = Instancio.create(OauthResponse.class);
        OAuthToken expectedToken = Instancio.create(OAuthToken.class);

        RequestTokenRequest expectedRequest = new RequestTokenRequest(
                oauth2ApplicationId,
                oauth2ApplicationSecret,
                authCode,
                "authorization_code",
                redirectUri
        );

        when(baseClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/oauth2/token")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(expectedRequest)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(OauthResponse.class)).thenReturn(oauthResponse);
        when(tokenMapper.map(oauthResponse)).thenReturn(expectedToken);

        OAuthToken oAuthToken = authClient.requestToken(authCode);

        assertNotNull(oAuthToken);
        assertEquals(expectedToken, oAuthToken);
    }

    @Test
    void itShouldRefreshAToken() {
        String refreshToken = "test-refresh-token";
        OauthResponse oauthResponse = Instancio.create(OauthResponse.class);
        OAuthToken expectedToken = Instancio.create(OAuthToken.class);

        RefreshTokenRequest expectedRequest = new RefreshTokenRequest(
                oauth2ApplicationId,
                oauth2ApplicationSecret,
                refreshToken,
                "refresh_token"
        );

        when(baseClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/oauth2/token")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(expectedRequest)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(OauthResponse.class)).thenReturn(oauthResponse);
        when(tokenMapper.map(oauthResponse)).thenReturn(expectedToken);

        OAuthToken oAuthToken = authClient.refreshToken(refreshToken);

        assertNotNull(oAuthToken);
        assertEquals(expectedToken, oAuthToken);
    }
}