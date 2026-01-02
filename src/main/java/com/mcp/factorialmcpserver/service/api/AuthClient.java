package com.mcp.factorialmcpserver.service.api;

import com.mcp.factorialmcpserver.model.auth.OauthToken;
import com.mcp.factorialmcpserver.service.api.response.OauthResponse;
import com.mcp.factorialmcpserver.service.api.response.mapper.TokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class AuthClient {

    private final RestClient restClient = RestClient.create();

    private final TokenMapper tokenMapper;

    private static final String CLIENT_ID = ""; // paste your client id here
    private static final String CLIENT_SECRET = ""; // paste your client secret here
    private static final String REDIRECT_URI = "http://127.0.0.1:7000/oauth2-callback";

    @Autowired
    public AuthClient(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    public OauthToken getOauthToken(String authCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);
        body.add("code", authCode);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", REDIRECT_URI);

        final OauthResponse response = restClient.post()
                .uri("https://api.factorialhr.com/oauth/token")
                .header("accept", "application/json")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(OauthResponse.class);
        return tokenMapper.map(response);
    }

}
