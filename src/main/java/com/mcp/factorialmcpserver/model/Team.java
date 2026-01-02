package com.mcp.factorialmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Team(
        Long id,
        String name,
        @JsonProperty("employee_ids") List<Long> employeeIds,
        @JsonProperty("lead_ids") List<Long> leadIds) {
}
