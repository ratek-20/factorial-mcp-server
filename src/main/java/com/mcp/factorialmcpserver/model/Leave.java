package com.mcp.factorialmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Leave(
        Long id,
        @JsonProperty("employee_id") Long employeeId,
        @JsonProperty("start_on") String startOn,
        @JsonProperty("finish_on") String finishOn,
        Boolean approved,
        @JsonProperty("employee_full_name") String employeeFullName,
        @JsonProperty(value = "half_day", defaultValue = "full_day") String partOfTheDay
) {}
