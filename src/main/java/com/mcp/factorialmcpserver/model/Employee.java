package com.mcp.factorialmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Employee(
        Long id,
        @JsonProperty("full_name") String fullName
) {}
