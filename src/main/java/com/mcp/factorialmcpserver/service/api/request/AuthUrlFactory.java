package com.mcp.factorialmcpserver.service.api.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class AuthUrlFactory {

    private final String redirectUri;
    private final String hostname;

    private static final String PROTOCOL = "https";
    private static final String PATH = "/oauth/authorize";

    // query params
    private static final String CLIENT_ID = "client_id";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String RESPONSE_TYPE = "response_type";
    private static final String STATE = "state";

    private static final String OAUTH2_APPLICATION_ID = ""; // paste your app id here
    private static final String CODE = "code";

    @Autowired
    public AuthUrlFactory(@Value("${factorial-api.redirect-uri}") String redirectUri,
                          @Value("${factorial-api.hostname}") String hostname) {
        this.redirectUri = redirectUri;
        this.hostname = hostname;
    }

    public URI create(String state) {
        return UriComponentsBuilder.newInstance()
                .scheme(PROTOCOL)
                .host(hostname)
                .path(PATH)
                .queryParam(CLIENT_ID, OAUTH2_APPLICATION_ID)
                .queryParam(REDIRECT_URI, redirectUri)
                .queryParam(RESPONSE_TYPE, CODE)
                .queryParam(STATE, state)
                .encode()
                .build()
                .toUri();
    }
}
