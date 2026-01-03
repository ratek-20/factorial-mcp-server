package com.mcp.factorialmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AllowanceStats(
        @JsonProperty("available_days") Double availableDays
) {}
