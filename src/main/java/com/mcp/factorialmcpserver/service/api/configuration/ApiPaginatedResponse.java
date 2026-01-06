package com.mcp.factorialmcpserver.service.api.configuration;

public record ApiPaginatedResponse<T>(
        T data,
        Object meta
) {}
