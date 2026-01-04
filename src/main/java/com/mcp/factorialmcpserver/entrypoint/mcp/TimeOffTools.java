package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.AllowanceStats;
import com.mcp.factorialmcpserver.model.Employee;
import com.mcp.factorialmcpserver.model.Leave;
import com.mcp.factorialmcpserver.model.LeaveType;
import com.mcp.factorialmcpserver.service.api.employees.EmployeesClient;
import com.mcp.factorialmcpserver.service.api.timeoff.TimeOffClient;
import com.mcp.factorialmcpserver.service.api.timeoff.request.LeaveRequest;
import com.mcp.factorialmcpserver.service.api.timeoff.request.UpdateLeaveRequest;
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


    /*
    The following tool names use the "time off" concept, even though they refer to "leaves" in the Factorial domain.
    I made this decision because I believe that "time off" is semantically more accurate when it comes to describing regular work absences.
     */

    @McpTool(name = "request_time_off", description = "Requests time off for the current user.")
    public String requestTimeOff(
            @McpToolParam(description = "The full name of the employee. This should be the name of the user currently using the agentic client.") String fullName,
            @McpToolParam(description = "The start date of the time off in YYYY-MM-DD format.") String startOn,
            @McpToolParam(description = "The finish date of the time off in YYYY-MM-DD format.") String finishOn,
            @McpToolParam(description = "If not provided, it will be automatically defaulted as 'vacation'.", required = false) Long leaveTypeId
    ) {
        final Employee employee = getEmployee(fullName);

        timeOffClient.requestLeave(new LeaveRequest(employee.id(), startOn, finishOn, leaveTypeId));

        return "Time off requested successfully for " + fullName + " from " + startOn + " to " + finishOn;
    }

    @McpTool(name = "get_leave_types", description = "Returns the list of available leave types.")
    public List<LeaveType> getLeaveTypes() {
        return timeOffClient.getLeaveTypes();
    }

    @McpTool(name = "read_time_offs", description = "Returns the list of time offs.")
    public List<Leave> readTimeOffRequests() {
        return timeOffClient.getLeaves();
    }

    @McpTool(name = "approve_time_off", description = "Approves a time off request.")
    public String approveTimeOff(Long leaveId) {
        timeOffClient.approveLeave(leaveId);
        return "Time off with ID " + leaveId + " has been approved.";
    }

    @McpTool(name = "update_time_off", description = "Updates a time off request.")
    public String updateTimeOff(Long leaveId, String newStartOn, String newFinishOn) {
        timeOffClient.updateLeave(leaveId, new UpdateLeaveRequest(leaveId, newStartOn, newFinishOn));
        return "Time off with ID " + leaveId + " has been updated successfully. New dates: " + newStartOn + " to " + newFinishOn;
    }

    @McpTool(name = "delete_time_off", description = "Deletes a time off request.")
    public String deleteTimeOff(Long leaveId) {
        timeOffClient.deleteLeave(leaveId);
        return "Time off with ID " + leaveId + " has been deleted successfully.";
    }

    private Employee getEmployee(String fullName) {
        return employeesClient.getEmployees().stream()
                .filter(e -> fullName.equalsIgnoreCase(e.fullName()))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with name: " + fullName));
    }
}
