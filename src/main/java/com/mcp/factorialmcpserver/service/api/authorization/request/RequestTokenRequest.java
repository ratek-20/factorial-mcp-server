package com.mcp.factorialmcpserver.service.api.authorization.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RequestTokenRequest(
        @JsonProperty("client_id") String oauth2AppId,
        @JsonProperty("client_secret") String oauth2AppSecret,
        @JsonProperty("code") String authCode,
        @JsonProperty("grant_type") String grantType,
        @JsonProperty("redirect_uri") String redirectUri
) {}
