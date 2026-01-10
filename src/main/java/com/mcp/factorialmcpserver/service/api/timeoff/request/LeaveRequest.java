package com.mcp.factorialmcpserver.service.api.timeoff.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mcp.factorialmcpserver.model.timeoff.HalfDay;

public record LeaveRequest(
    @JsonProperty("employee_id") Long employeeId,
    @JsonProperty("start_on") String startOn,
    @JsonProperty("finish_on") String finishOn,
    @JsonProperty("leave_type_id") Long leaveTypeId,
    @JsonProperty("half_day") HalfDay halfDay
) {}
