package com.mcp.factorialmcpserver.model.auth;

import java.time.Instant;

public record AccessToken(String accessToken, Instant expiresAt) {

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
