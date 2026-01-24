package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.credentials.EmployeeCredentials;
import com.mcp.factorialmcpserver.model.employees.Employee;
import com.mcp.factorialmcpserver.service.api.credentials.CredentialsClient;
import com.mcp.factorialmcpserver.service.api.employees.EmployeesClient;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeToolsTest {

    @Mock
    private CredentialsClient credentialsClient;

    @Mock
    private EmployeesClient employeesClient;

    @InjectMocks
    private EmployeeTools employeeTools;

    @Test
    void itShouldReturnCurrentEmployee() {
        EmployeeCredentials expectedCredentials = Instancio.create(EmployeeCredentials.class);
        when(credentialsClient.getCurrentEmployee()).thenReturn(expectedCredentials);

        EmployeeCredentials employeeCredentials = employeeTools.getCurrentEmployee();

        assertEquals(expectedCredentials, employeeCredentials);
    }

    @Test
    void itShouldReturnEmployeeByFullName() {
        String fullName = "Alice Johnson";
        Employee expectedEmployee = Instancio.create(Employee.class);
        when(employeesClient.getEmployee(fullName)).thenReturn(expectedEmployee);

        Employee employee = employeeTools.getEmployee(fullName);

        assertEquals(expectedEmployee, employee);
    }
}
