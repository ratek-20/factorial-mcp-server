package com.mcp.factorialmcpserver.service.api.authorization;

import com.mcp.factorialmcpserver.model.auth.OauthToken;
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

    private static final String BASE_PATH = "/oauth2/token";

    private static final String OAUTH2_APPLICATION_ID = ""; // paste your client id here
    private static final String OAUTH2_APPLICATION_SECRET = ""; // paste your client secret here
    private static final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    @Autowired
    public AuthClient(RestClient baseClient,
                      TokenMapper tokenMapper,
                      @Value("${factorial-api.redirect-uri}") String redirectUri
    ) {
        this.baseClient = baseClient;
        this.tokenMapper = tokenMapper;
        this.redirectUri = redirectUri;
    }

    public OauthToken requestToken(String authCode) {
        final RequestTokenRequest request = new RequestTokenRequest(OAUTH2_APPLICATION_ID, OAUTH2_APPLICATION_SECRET, authCode, AUTHORIZATION_CODE_GRANT_TYPE, redirectUri);
        final OauthResponse response = baseClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(request)
                .retrieve()
                .body(OauthResponse.class);
        return tokenMapper.map(response);
    }

    public OauthToken refreshToken(String refreshToken) {
        final RefreshTokenRequest request = new RefreshTokenRequest(OAUTH2_APPLICATION_ID, OAUTH2_APPLICATION_SECRET, refreshToken, REFRESH_TOKEN_GRANT_TYPE);
        final OauthResponse response = baseClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(request)
                .retrieve()
                .body(OauthResponse.class);
        return tokenMapper.map(response);
    }

}
