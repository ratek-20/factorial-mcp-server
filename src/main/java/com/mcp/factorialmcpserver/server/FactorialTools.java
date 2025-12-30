package com.mcp.factorialmcpserver.server;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
public class FactorialTools {

    @McpTool(name = "get_employees", description = "Returns the list of the employees of the company.")
    public String getEmployees() {
        return """
                Alice,
                Bob,
                Carla
                """;
    }
}
