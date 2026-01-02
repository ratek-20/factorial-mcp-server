package com.mcp.factorialmcpserver.service.api.response;

public record OauthResponse(
        String access_token,
        Long expires_in,
        String refresh_token
) {}
