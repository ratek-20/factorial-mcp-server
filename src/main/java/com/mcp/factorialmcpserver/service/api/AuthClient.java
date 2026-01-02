package com.mcp.factorialmcpserver.service.api;

import com.mcp.factorialmcpserver.model.auth.OauthToken;
import com.mcp.factorialmcpserver.service.api.request.OauthRequest;
import com.mcp.factorialmcpserver.service.api.response.OauthResponse;
import com.mcp.factorialmcpserver.service.api.response.mapper.TokenMapper;
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
    private static final String GRANT_TYPE = "authorization_code";

    @Autowired
    public AuthClient(RestClient baseClient,
                      TokenMapper tokenMapper,
                      @Value("${factorial-api.redirect-uri}") String redirectUri
    ) {
        this.baseClient = baseClient;
        this.tokenMapper = tokenMapper;
        this.redirectUri = redirectUri;
    }

    public OauthToken getOauthToken(String authCode) {
        final OauthRequest oauthRequest = new OauthRequest(OAUTH2_APPLICATION_ID, OAUTH2_APPLICATION_SECRET, authCode, GRANT_TYPE, redirectUri);
        final OauthResponse response = baseClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(oauthRequest)
                .retrieve()
                .body(OauthResponse.class);
        return tokenMapper.map(response);
    }

}
