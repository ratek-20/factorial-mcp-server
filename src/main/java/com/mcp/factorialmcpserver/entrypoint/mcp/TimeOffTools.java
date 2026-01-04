package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.AllowanceStats;
import com.mcp.factorialmcpserver.model.Employee;
import com.mcp.factorialmcpserver.service.api.employees.EmployeesClient;
import com.mcp.factorialmcpserver.service.api.timeoff.TimeOffClient;
import com.mcp.factorialmcpserver.service.api.timeoff.request.TimeOffRequest;
import com.mcp.factorialmcpserver.service.exception.EmployeeNotFoundException;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimeOffTools {

    private final TimeOffClient timeOffClient;
    private final EmployeesClient employeesClient;

    @Autowired
    public TimeOffTools(TimeOffClient timeOffClient, EmployeesClient employeesClient) {
        this.timeOffClient = timeOffClient;
        this.employeesClient = employeesClient;
    }

    @McpTool(name = "get_available_vacation_days", description = "Returns the available vacation days for the current user.")
    public Double getAvailableVacationDays(
            @McpToolParam(description = "The full name of the employee. This should be the name of the user currently using the agentic client.") String fullName
    ) {
        final Employee employee = getEmployee(fullName);

        final List<AllowanceStats> stats = timeOffClient.getAllowanceStats(employee.id());

        return stats.stream()
                .findFirst()
                .map(AllowanceStats::availableDays)
                .orElse(0.0);
    }

    @McpTool(name = "request_time_off", description = "Requests time off for the current user.")
    public String requestTimeOff(
            @McpToolParam(description = "The full name of the employee. This should be the name of the user currently using the agentic client.") String fullName,
            @McpToolParam(description = "The start date of the time off in YYYY-MM-DD format.") String startOn,
            @McpToolParam(description = "The finish date of the time off in YYYY-MM-DD format.") String finishOn
    ) {
        final Employee employee = getEmployee(fullName);

        timeOffClient.requestTimeOff(new TimeOffRequest(employee.id(), startOn, finishOn));

        return "Time off requested successfully for " + fullName + " from " + startOn + " to " + finishOn;
    }

    private Employee getEmployee(String fullName) {
        return employeesClient.getEmployees().stream()
                .filter(e -> fullName.equalsIgnoreCase(e.fullName()))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with name: " + fullName));
    }
}
