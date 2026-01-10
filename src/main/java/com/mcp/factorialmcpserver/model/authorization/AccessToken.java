package com.mcp.factorialmcpserver.model.authorization;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

public record AccessToken(String accessToken, Instant expiresAt) {

    @JsonIgnore
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
