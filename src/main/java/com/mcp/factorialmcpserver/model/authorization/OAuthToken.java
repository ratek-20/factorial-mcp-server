package com.mcp.factorialmcpserver.model.authorization;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record OAuthToken(AccessToken accessToken, String refreshToken) {

    @JsonIgnore
    public boolean isAccessTokenExpired() {
        return accessToken.isExpired();
    }

    @JsonIgnore
    public String accessTokenValue() {
        return accessToken.accessToken();
    }

}
