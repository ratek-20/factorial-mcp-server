package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.service.api.employees.EmployeesClient;
import com.mcp.factorialmcpserver.model.Employee;
import com.mcp.factorialmcpserver.service.api.legalentities.LegalEntitiesClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeTools {

    private final EmployeesClient employeesClient;
    private final LegalEntitiesClient legalEntitiesClient;

    @Autowired
    public EmployeeTools(EmployeesClient employeesClient, LegalEntitiesClient legalEntitiesClient) {
        this.employeesClient = employeesClient;
        this.legalEntitiesClient = legalEntitiesClient;
    }

    @McpTool(name = "get_employees", description = "Returns the list of the employees of the company.")
    public List<Employee> getEmployees() {
        return employeesClient.getEmployees();
    }

    @McpTool(name = "create_employee", description = "Creates a new employee.")
    public Employee createEmployee(String firstName, String lastName, String email) {
        final Long companyId = legalEntitiesClient.getCompanyId();
        return employeesClient.createEmployee(firstName, lastName, email, companyId);
    }
}
