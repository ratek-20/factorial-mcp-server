package com.mcp.factorialmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LegalEntity(
        @JsonProperty("company_id") Long companyId
) {}
