package com.mcp.factorialmcpserver.model.authorization;

public record InteractiveAuthorizationResult(boolean browserOpened, String authUrl) {
}
