package com.mcp.factorialmcpserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum HalfDay {
    @JsonProperty("beggining_of_day") MORNING, // There's a typo in the public API
    @JsonProperty("end_of_day") AFTERNOON
}
