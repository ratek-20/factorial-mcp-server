package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.service.api.employees.EmployeesClient;
import com.mcp.factorialmcpserver.model.Employee;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeTools {

    private final EmployeesClient employeesClient;

    @Autowired
    public EmployeeTools(EmployeesClient employeesClient) {
        this.employeesClient = employeesClient;
    }

    @McpTool(name = "get_employees", description = "Returns the list of the employees of the company.")
    public List<Employee> getEmployees() {
        return employeesClient.getEmployees();
    }
}
