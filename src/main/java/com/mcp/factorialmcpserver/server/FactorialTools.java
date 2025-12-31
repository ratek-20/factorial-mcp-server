package com.mcp.factorialmcpserver.server;

import com.mcp.factorialmcpserver.api.FactorialClient;
import com.mcp.factorialmcpserver.model.Employee;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FactorialTools {

    private final FactorialClient factorialClient;

    @Autowired
    public FactorialTools(FactorialClient factorialClient) {
        this.factorialClient = factorialClient;
    }

    @McpTool(name = "get_employees", description = "Returns the list of the employees of the company.")
    public List<Employee> getEmployees() {
        return factorialClient.getEmployees();
    }
}
