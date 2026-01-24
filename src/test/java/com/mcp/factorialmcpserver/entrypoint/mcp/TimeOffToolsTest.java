package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.timeoff.AllowanceStats;
import com.mcp.factorialmcpserver.model.timeoff.HalfDay;
import com.mcp.factorialmcpserver.model.timeoff.Leave;
import com.mcp.factorialmcpserver.model.timeoff.LeaveType;
import com.mcp.factorialmcpserver.service.api.timeoff.TimeOffClient;
import com.mcp.factorialmcpserver.service.api.timeoff.request.LeaveRequest;
import com.mcp.factorialmcpserver.service.api.timeoff.request.UpdateLeaveRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeOffToolsTest {

    @Mock
    private TimeOffClient timeOffClient;

    @InjectMocks
    private TimeOffTools timeOffTools;

    @Test
    void itShouldReturnAvailableVacationDays() {
        Long employeeId = 1L;
        AllowanceStats stats = Instancio.of(AllowanceStats.class)
                .set(field(AllowanceStats::availableDays), 15.5)
                .create();
        when(timeOffClient.getAllowanceStats(employeeId)).thenReturn(List.of(stats));

        Double availableDays = timeOffTools.getAvailableVacationDays(employeeId);

        assertEquals(15.5, availableDays);
    }

    @Test
    void itShouldReturnZeroWhenNoAllowanceStatsFound() {
        Long employeeId = 1L;
        when(timeOffClient.getAllowanceStats(employeeId)).thenReturn(List.of());

        Double availableDays = timeOffTools.getAvailableVacationDays(employeeId);

        assertEquals(0.0, availableDays);
    }

    @Test
    void itShouldRequestTimeOff() {
        Long employeeId = 1L;
        String startOn = "2026-01-01";
        String finishOn = "2026-01-05";
        Long leaveTypeId = 2L;
        HalfDay halfDay = HalfDay.MORNING;
        LeaveRequest expectedRequest = new LeaveRequest(employeeId, startOn, finishOn, leaveTypeId, halfDay);

        String message = timeOffTools.requestTimeOff(employeeId, startOn, finishOn, leaveTypeId, halfDay);

        verify(timeOffClient).requestLeave(expectedRequest);
        assertEquals("Time off requested successfully from 2026-01-01 to 2026-01-05", message);
    }

    @Test
    void itShouldGetLeaveTypes() {
        List<LeaveType> leaveTypes = Instancio.ofList(LeaveType.class).create();
        when(timeOffClient.getLeaveTypes()).thenReturn(leaveTypes);

        List<LeaveType> actualLeaveTypes = timeOffTools.getLeaveTypes();

        assertEquals(leaveTypes, actualLeaveTypes);
    }

    @Test
    void itShouldReadTimeOffs() {
        Long employeeId = 1L;
        List<Leave> leaves = Instancio.ofList(Leave.class).create();
        when(timeOffClient.getLeaves(employeeId)).thenReturn(leaves);

        List<Leave> actualLeaves = timeOffTools.readTimeOffs(employeeId);

        assertEquals(leaves, actualLeaves);
    }

    @Test
    void itShouldApproveTimeOff() {
        Long leaveId = 123L;

        String message = timeOffTools.approveTimeOff(leaveId);

        verify(timeOffClient).approveLeave(leaveId);
        assertEquals("Time off with ID 123 has been approved.", message);
    }

    @Test
    void itShouldUpdateTimeOff() {
        Long leaveId = 123L;
        String newStartOn = "2026-02-01";
        String newFinishOn = "2026-02-05";
        Long leaveTypeId = 2L;
        HalfDay halfDay = HalfDay.AFTERNOON;
        UpdateLeaveRequest expectedRequest = new UpdateLeaveRequest(leaveId, newStartOn, newFinishOn, leaveTypeId, halfDay);

        String message = timeOffTools.updateTimeOff(leaveId, newStartOn, newFinishOn, leaveTypeId, halfDay);

        verify(timeOffClient).updateLeave(leaveId, expectedRequest);
        assertEquals("Time off with ID 123 has been updated successfully. New dates: 2026-02-01 to 2026-02-05", message);
    }

    @Test
    void itShouldDeleteTimeOff() {
        Long leaveId = 123L;

        String message = timeOffTools.deleteTimeOff(leaveId);

        verify(timeOffClient).deleteLeave(leaveId);
        assertEquals("Time off with ID 123 has been deleted successfully.", message);
    }
}
