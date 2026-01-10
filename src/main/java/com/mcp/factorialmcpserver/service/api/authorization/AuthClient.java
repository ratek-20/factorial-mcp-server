package com.mcp.factorialmcpserver.service.api.authorization;

import com.mcp.factorialmcpserver.model.authorization.OAuthToken;
import com.mcp.factorialmcpserver.service.api.authorization.mapper.TokenMapper;
import com.mcp.factorialmcpserver.service.api.authorization.request.RefreshTokenRequest;
import com.mcp.factorialmcpserver.service.api.authorization.request.RequestTokenRequest;
import com.mcp.factorialmcpserver.service.api.authorization.response.OauthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthClient {

    private final RestClient baseClient;
    private final TokenMapper tokenMapper;
    private final String redirectUri;
    private final String oauth2ApplicationId;  // env var
    private final String oauth2ApplicationSecret;  // env var

    private static final String BASE_PATH = "/oauth2/token";

    private static final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    @Autowired
    public AuthClient(RestClient baseClient,
                      TokenMapper tokenMapper,
                      @Value("${factorial-api.redirect-uri}") String redirectUri,
                      @Value("${OAUTH2_APPLICATION_ID}") String oauth2ApplicationId,
                      @Value("${OAUTH2_APPLICATION_SECRET}") String oauth2ApplicationSecret
    ) {
        this.baseClient = baseClient;
        this.tokenMapper = tokenMapper;
        this.redirectUri = redirectUri;
        this.oauth2ApplicationId = oauth2ApplicationId;
        this.oauth2ApplicationSecret = oauth2ApplicationSecret;
    }

    public OAuthToken requestToken(String authCode) {
        final RequestTokenRequest request = new RequestTokenRequest(
                oauth2ApplicationId,
                oauth2ApplicationSecret,
                authCode,
                AUTHORIZATION_CODE_GRANT_TYPE,
                redirectUri
        );
        final OauthResponse response = baseClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(request)
                .retrieve()
                .body(OauthResponse.class);
        return tokenMapper.map(response);
    }

    public OAuthToken refreshToken(String refreshToken) {
        final RefreshTokenRequest request = new RefreshTokenRequest(
                oauth2ApplicationId,
                oauth2ApplicationSecret,
                refreshToken,
                REFRESH_TOKEN_GRANT_TYPE
        );
        final OauthResponse response = baseClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(request)
                .retrieve()
                .body(OauthResponse.class);
        return tokenMapper.map(response);
    }

}
