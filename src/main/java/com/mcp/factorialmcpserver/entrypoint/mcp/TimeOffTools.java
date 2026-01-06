package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.AllowanceStats;
import com.mcp.factorialmcpserver.model.HalfDay;
import com.mcp.factorialmcpserver.model.Leave;
import com.mcp.factorialmcpserver.model.LeaveType;
import com.mcp.factorialmcpserver.service.api.timeoff.TimeOffClient;
import com.mcp.factorialmcpserver.service.api.timeoff.request.LeaveRequest;
import com.mcp.factorialmcpserver.service.api.timeoff.request.UpdateLeaveRequest;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TimeOffTools {

    private final TimeOffClient timeOffClient;

    @Autowired
    public TimeOffTools(TimeOffClient timeOffClient) {
        this.timeOffClient = timeOffClient;
    }

    @McpTool(name = "get_available_vacation_days", description = "Returns the available vacation days for the current user.")
    public Double getAvailableVacationDays(Long employeeId) {
        final List<AllowanceStats> stats = timeOffClient.getAllowanceStats(employeeId);
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
            Long employeeId,
            @McpToolParam(description = "The start date of the time off in YYYY-MM-DD format.") String startOn,
            @McpToolParam(description = "The finish date of the time off in YYYY-MM-DD format.") String finishOn,
            @McpToolParam(description = "If not provided, it will be automatically defaulted as 'vacation'.", required = false) Long leaveTypeId,
            @McpToolParam(description = "If not provided, it will be automatically defaulted as 'full day'.", required = false) HalfDay halfDay
    ) {
        timeOffClient.requestLeave(new LeaveRequest(employeeId, startOn, finishOn, leaveTypeId, halfDay));
        return "Time off requested successfully from " + startOn + " to " + finishOn;
    }

    @McpTool(name = "get_leave_types", description = "Returns the list of available leave types.")
    public List<LeaveType> getLeaveTypes() {
        return timeOffClient.getLeaveTypes();
    }

    @McpTool(name = "read_time_offs", description = "Returns the list of time offs for the given employee.")
    public List<Leave> readTimeOffs(Long employeeId) {
        final String from = LocalDate.now().minusYears(1).toString(); // do not fetch time offs older than 1 year
        return timeOffClient.getLeaves(employeeId, from);
    }

    @McpTool(name = "approve_time_off", description = "Approves a time off request.")
    public String approveTimeOff(Long leaveId) {
        timeOffClient.approveLeave(leaveId);
        return "Time off with ID " + leaveId + " has been approved.";
    }

    @McpTool(name = "update_time_off", description = "Updates a time off request.")
    public String updateTimeOff(
            Long leaveId,
            @McpToolParam(description = "The new start date of the time off in YYYY-MM-DD format.") String newStartOn,
            @McpToolParam(description = "The new end date of the time off in YYYY-MM-DD format.") String newFinishOn,
            @McpToolParam(description = "If not provided, it will be automatically defaulted as 'vacation'.", required = false) Long leaveTypeId,
            @McpToolParam(description = "If not provided, it will be automatically defaulted as 'full day'.", required = false) HalfDay halfDay
    ) {
        timeOffClient.updateLeave(leaveId, new UpdateLeaveRequest(leaveId, newStartOn, newFinishOn, leaveTypeId, halfDay));
        return "Time off with ID " + leaveId + " has been updated successfully. New dates: " + newStartOn + " to " + newFinishOn;
    }

    @McpTool(name = "delete_time_off", description = "Deletes a time off request.")
    public String deleteTimeOff(Long leaveId) {
        timeOffClient.deleteLeave(leaveId);
        return "Time off with ID " + leaveId + " has been deleted successfully.";
    }

}
