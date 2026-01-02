package com.mcp.factorialmcpserver.model.auth;

public record OauthToken(AccessToken accessToken, String refreshToken) {

    public boolean isAccessTokenExpired() {
        return accessToken.isExpired();
    }

    public String getAccessToken() {
        return accessToken.accessToken();
    }

}
