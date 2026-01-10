package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.employees.Employee;
import com.mcp.factorialmcpserver.model.credentials.EmployeeCredentials;
import com.mcp.factorialmcpserver.service.api.credentials.CredentialsClient;
import com.mcp.factorialmcpserver.service.api.employees.EmployeesClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeTools {

    private final CredentialsClient credentialsClient;
    private final EmployeesClient employeesClient;

    @Autowired
    public EmployeeTools(CredentialsClient credentialsClient, EmployeesClient employeesClient) {
        this.credentialsClient = credentialsClient;
        this.employeesClient = employeesClient;
    }

    @McpTool(name = "get_current_employee", description = "Returns the full name and employee id for the current user.")
    public EmployeeCredentials getCurrentEmployee() {
        return credentialsClient.getCurrentEmployee();
    }

    @McpTool(name = "get_employee", description = "Returns an employee by full name.")
    public Employee getEmployee(
            @McpToolParam(description = "The full name of the employee to search for. Example: Alice Johnson") String fullName
    ) {
        return employeesClient.getEmployee(fullName);
    }

}
