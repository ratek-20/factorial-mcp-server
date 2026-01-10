package com.mcp.factorialmcpserver.model.employees;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Employee(
    @JsonProperty("full_name") String fullName,
    Long id
) {}
