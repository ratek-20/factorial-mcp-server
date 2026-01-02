package com.mcp.factorialmcpserver.service.api.employees;

import com.mcp.factorialmcpserver.model.Employee;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.GenericApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeesClient {

    private final RestClient baseClient;
    private final AuthManager authManager;

    private static final String BASE_PATH = "/resources/employees/employees";

    // query params
    private static final String ONLY_ACTIVE = "only_active";
    private static final String ONLY_MANAGERS = "only_managers";

    @Value( "${factorial-api.api-key}")
    private String apiKey; // TODO: remove when oauth flow is available

    @Autowired
    public EmployeesClient(RestClient baseClient, AuthManager authManager) {
        this.baseClient = baseClient;
        this.authManager = authManager;
    }

    public List<Employee> getEmployees() {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        final GenericApiResponse<List<Employee>> response = baseClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH)
                        .queryParam(ONLY_ACTIVE, true)
                        .queryParam(ONLY_MANAGERS, false)
                        .build())
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

}
