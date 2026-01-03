package com.mcp.factorialmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Membership(
        Long id,
        @JsonProperty("employee_id") Long employeeId,
        @JsonProperty("team_id") Long teamId
) {}
