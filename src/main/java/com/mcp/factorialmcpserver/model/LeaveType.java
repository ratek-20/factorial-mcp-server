package com.mcp.factorialmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LeaveType(
    Long id,
    String name
) {}
