package com.mcp.factorialmcpserver.service.api.response;

public record ApiResponse<T>(
        T data,
        Object meta
) {}
