package com.mcp.factorialmcpserver.service.api.timeoff;

import com.mcp.factorialmcpserver.model.AllowanceStats;
import com.mcp.factorialmcpserver.model.Leave;
import com.mcp.factorialmcpserver.model.LeaveType;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.GenericApiResponse;
import com.mcp.factorialmcpserver.service.api.timeoff.request.ApproveLeaveRequest;
import com.mcp.factorialmcpserver.service.api.timeoff.request.LeaveRequest;
import com.mcp.factorialmcpserver.service.api.timeoff.request.UpdateLeaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TimeOffClient {

    private final RestClient baseClient;
    private final AuthManager authManager;

    private static final String COMMON_ROOT = "/resources/timeoff";
    private static final String LEAVES_PATH = "/leaves";

    @Value("${factorial-api.api-key}")
    private String apiKey; // TODO: remove when oauth flow is available

    @Autowired
    public TimeOffClient(RestClient baseClient, AuthManager authManager) {
        this.baseClient = baseClient;
        this.authManager = authManager;
    }

    public List<AllowanceStats> getAllowanceStats(Long employeeId) {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        final String allowanceStatsPath = "/allowance_stats";
        final String employeeIdsParam = "employee_ids[]";
        final GenericApiResponse<List<AllowanceStats>> response = baseClient.get()
                .uri(uriBuilder -> uriBuilder.path(COMMON_ROOT + allowanceStatsPath)
                        .queryParam(employeeIdsParam, employeeId)
                        .build()
                )
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            return List.of();
        }
        return Optional.ofNullable(response.data())
                .orElse(List.of());
    }

    public void requestLeave(LeaveRequest request) {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        baseClient.post()
                .uri(COMMON_ROOT + LEAVES_PATH)
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public List<Leave> getLeaves() {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        final String includeDeletedQueryParam = "include_deleted_leaves";
        final GenericApiResponse<List<Leave>> response = baseClient.get()
                .uri(uriBuilder -> uriBuilder.path(COMMON_ROOT + LEAVES_PATH)
                        .queryParam(includeDeletedQueryParam, false)
                        .build())
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            return List.of();
        }
        return Optional.ofNullable(response.data())
                .orElse(List.of());
    }

    public void approveLeave(Long id) {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        final String approvePath = "/approve";
        baseClient.post()
                .uri(COMMON_ROOT + LEAVES_PATH + approvePath)
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .body(new ApproveLeaveRequest(id))
                .retrieve()
                .toBodilessEntity();
    }

    public void updateLeave(Long id, UpdateLeaveRequest request) {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        baseClient.put()
                .uri(COMMON_ROOT + LEAVES_PATH + "/" + id)
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteLeave(Long id) {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        baseClient.delete()
                .uri(COMMON_ROOT + LEAVES_PATH + "/" + id)
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();
    }

    public List<LeaveType> getLeaveTypes() {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        final String leaveTypesPath = "/leave_types";
        final GenericApiResponse<List<LeaveType>> response = baseClient.get()
                .uri(COMMON_ROOT + leaveTypesPath)
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            return List.of();
        }
        return Optional.ofNullable(response.data())
                .orElse(List.of());
    }

}
