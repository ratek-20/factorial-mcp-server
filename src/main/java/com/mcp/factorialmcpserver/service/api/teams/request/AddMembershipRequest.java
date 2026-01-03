package com.mcp.factorialmcpserver.service.api.teams.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddMembershipRequest(
        @JsonProperty("employee_id") Long employeeId,
        @JsonProperty("team_id") Long teamId
) {}
