package com.mcp.factorialmcpserver.model.auth;

public record OAuthToken(AccessToken accessToken, String refreshToken) {

    public boolean isAccessTokenExpired() {
        return accessToken.isExpired();
    }

    public String getAccessToken() {
        return accessToken.accessToken();
    }

}
