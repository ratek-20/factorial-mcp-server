package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.service.api.employees.EmployeeClient;
import com.mcp.factorialmcpserver.model.Employee;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FactorialTools {

    private final EmployeeClient employeeClient;

    @Autowired
    public FactorialTools(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }

    @McpTool(name = "get_employees", description = "Returns the list of the employees of the company.")
    public List<Employee> getEmployees() {
        return employeeClient.getEmployees();
    }
}
