package com.mcp.factorialmcpserver.service.api.timeoff;

import com.mcp.factorialmcpserver.model.timeoff.AllowanceStats;
import com.mcp.factorialmcpserver.model.timeoff.Leave;
import com.mcp.factorialmcpserver.model.timeoff.LeaveType;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.ApiPaginatedResponse;
import com.mcp.factorialmcpserver.service.api.timeoff.request.ApproveLeaveRequest;
import com.mcp.factorialmcpserver.service.api.timeoff.request.LeaveRequest;
import com.mcp.factorialmcpserver.service.api.timeoff.request.UpdateLeaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TimeOffClient {

    private final RestClient baseClient;
    private final AuthManager authManager;

    private volatile List<LeaveType> cachedLeaveTypes;

    private static final String COMMON_ROOT = "/resources/timeoff";
    private static final String LEAVES_PATH = "/leaves";

    @Autowired
    public TimeOffClient(RestClient baseClient, AuthManager authManager) {
        this.baseClient = baseClient;
        this.authManager = authManager;
    }

    public List<AllowanceStats> getAllowanceStats(Long employeeId) {
        final String accessToken = authManager.getValidAccessToken();
        final String allowanceStatsPath = "/allowance_stats";
        final String employeeIdsParam = "employee_ids[]";
        final ApiPaginatedResponse<List<AllowanceStats>> response = baseClient.get()
                .uri(uriBuilder -> uriBuilder.path(COMMON_ROOT + allowanceStatsPath)
                        .queryParam(employeeIdsParam, employeeId)
                        .build()
                )
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            return List.of();
        }
        return Optional.ofNullable(response.data())
                .orElse(List.of());
    }

    public void requestLeave(LeaveRequest request) {
        final String accessToken = authManager.getValidAccessToken();
        baseClient.post()
                .uri(COMMON_ROOT + LEAVES_PATH)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public List<Leave> getLeaves(Long employeeId) {
        final String accessToken = authManager.getValidAccessToken();
        final String includeDeletedQueryParam = "include_deleted_leaves";
        final String employeeIdsParam = "employee_ids[]";
        final String fromParam = "from";
        final String notOlderThanOneYear = LocalDate.now().minusYears(1).toString();
        final ApiPaginatedResponse<List<Leave>> response = baseClient.get()
                .uri(uriBuilder -> uriBuilder.path(COMMON_ROOT + LEAVES_PATH)
                        .queryParam(includeDeletedQueryParam, false)
                        .queryParam(employeeIdsParam, employeeId)
                        .queryParam(fromParam, notOlderThanOneYear)
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            return List.of();
        }
        return Optional.ofNullable(response.data())
                .orElse(List.of());
    }

    public void approveLeave(Long id) {
        final String accessToken = authManager.getValidAccessToken();
        final String approvePath = "/approve";
        baseClient.post()
                .uri(COMMON_ROOT + LEAVES_PATH + approvePath)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .body(new ApproveLeaveRequest(id))
                .retrieve()
                .toBodilessEntity();
    }

    public void updateLeave(Long id, UpdateLeaveRequest request) {
        final String accessToken = authManager.getValidAccessToken();
        baseClient.put()
                .uri(COMMON_ROOT + LEAVES_PATH + "/" + id)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteLeave(Long id) {
        final String accessToken = authManager.getValidAccessToken();
        baseClient.delete()
                .uri(COMMON_ROOT + LEAVES_PATH + "/" + id)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();
    }

    public List<LeaveType> getLeaveTypes() {
        if (Objects.nonNull(cachedLeaveTypes)) {
            return cachedLeaveTypes;
        }
        synchronized (this) {
            if (Objects.isNull(cachedLeaveTypes)) {
                cachedLeaveTypes = getLeaveTypesFromApi();
            }
            return cachedLeaveTypes;
        }
    }

    private List<LeaveType> getLeaveTypesFromApi() {
        final String accessToken = authManager.getValidAccessToken();
        final String leaveTypesPath = "/leave_types";
        final ApiPaginatedResponse<List<LeaveType>> response = baseClient.get()
                .uri(COMMON_ROOT + leaveTypesPath)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            return List.of();
        }
        return Optional.ofNullable(response.data())
                .orElse(List.of());
    }

}
