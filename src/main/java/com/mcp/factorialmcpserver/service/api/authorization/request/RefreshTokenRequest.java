package com.mcp.factorialmcpserver.service.api.authorization.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshTokenRequest(
        @JsonProperty("client_id") String oauth2AppId,
        @JsonProperty("client_secret") String oauth2AppSecret,
        @JsonProperty("grant_type") String grantType,
        @JsonProperty("refresh_token") String refreshToken
) {}
