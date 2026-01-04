package com.mcp.factorialmcpserver.service.api.timeoff.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateLeaveRequest(
    Long id,
    @JsonProperty("start_on") String startOn,
    @JsonProperty("finish_on") String finishOn
) {}
