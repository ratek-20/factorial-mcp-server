package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.Employee;
import com.mcp.factorialmcpserver.model.EmployeeCredentials;
import com.mcp.factorialmcpserver.service.api.credentials.CredentialsClient;
import com.mcp.factorialmcpserver.service.api.employees.EmployeesClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeTools {

    private final EmployeesClient employeesClient;
    private final CredentialsClient credentialsClient;

    @Autowired
    public EmployeeTools(EmployeesClient employeesClient, CredentialsClient credentialsClient) {
        this.employeesClient = employeesClient;
        this.credentialsClient = credentialsClient;
    }

    @McpTool(name = "get_current_employee", description = "Returns the full name and employee id for the current user.")
    public EmployeeCredentials getCurrentEmployee() {
        return credentialsClient.getCurrentEmployee();
    }

    @McpTool(name = "get_employees", description = "Returns the list of the employees of the company.")
    public List<Employee> getEmployees() {
        return employeesClient.getEmployees();
    }

}
