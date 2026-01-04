package com.mcp.factorialmcpserver.service.api.timeoff.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LeaveRequest(
    @JsonProperty("employee_id") Long employeeId,
    @JsonProperty("start_on") String startOn,
    @JsonProperty("finish_on") String finishOn
) {}
