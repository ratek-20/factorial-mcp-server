package com.mcp.factorialmcpserver.model.credentials;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmployeeCredentials(
    @JsonProperty("full_name") String fullName,
    @JsonProperty("employee_id") Long employeeId
) {}
