package com.mcp.factorialmcpserver.service.api.employees.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateEmployeeRequest(
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        String email,
        @JsonProperty("company_id") Long companyId
) {}
