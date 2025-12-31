package com.mcp.factorialmcpserver.api;

import com.mcp.factorialmcpserver.api.response.ApiResponse;
import com.mcp.factorialmcpserver.model.Employee;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FactorialClient {

    private static final String API_KEY = ""; // paste your api key here

    public List<Employee> getEmployees() {
        RestClient restClient = RestClient.create();
        ApiResponse<List<Employee>> response = restClient.get()
                .uri("https://api.factorialhr.com/api/2025-10-01/resources/employees/employees?only_active=true&only_managers=false")
                .header("accept", "application/json")
                .header("x-api-key", API_KEY)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            return List.of();
        }
        return Optional.ofNullable(response.data())
                .orElse(List.of());
    }

}
