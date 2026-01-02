package com.mcp.factorialmcpserver.service.api;

import com.mcp.factorialmcpserver.model.Employee;
import com.mcp.factorialmcpserver.service.api.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeClient {

    private final RestClient restClient = RestClient.create();

    private static final String API_KEY = ""; // paste your api key here

    private final AuthManager authManager;

    @Autowired
    public EmployeeClient(AuthManager authManager) {
        this.authManager = authManager;
    }

    public List<Employee> getEmployees() {
        final String accessToken = authManager.getValidAccessToken();
        final ApiResponse<List<Employee>> response = restClient.get()
                .uri("https://api.factorialhr.com/api/2025-10-01/resources/employees/employees?only_active=true&only_managers=false")
                .header("accept", "application/json")
                .header("authorization", "Bearer " + accessToken)
                //.header("x-api-key", API_KEY)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            return List.of();
        }
        return Optional.ofNullable(response.data())
                .orElse(List.of());
    }

}
