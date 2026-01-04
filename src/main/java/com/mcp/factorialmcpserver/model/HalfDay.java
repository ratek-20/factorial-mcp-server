package com.mcp.factorialmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum HalfDay {
    @JsonProperty("beginning_of_day") MORNING,
    @JsonProperty("end_of_day") AFTERNOON
}
