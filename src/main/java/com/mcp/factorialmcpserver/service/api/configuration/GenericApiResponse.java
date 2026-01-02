package com.mcp.factorialmcpserver.service.api.configuration;

public record GenericApiResponse<T>(
        T data,
        Object meta
) {}
