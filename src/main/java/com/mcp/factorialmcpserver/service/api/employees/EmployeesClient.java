package com.mcp.factorialmcpserver.service.api.employees;

import com.mcp.factorialmcpserver.model.employees.Employee;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.ApiPaginatedResponse;
import com.mcp.factorialmcpserver.service.exception.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
public class EmployeesClient {

    private final RestClient baseClient;
    private final AuthManager authManager;

    private static final String BASE_PATH = "/resources/employees/employees";

    // query params
    private static final String ONLY_ACTIVE = "only_active";
    private static final String ONLY_MANAGERS = "only_managers";
    private static final String FULL_TEXT_NAME = "full_text_name";

    @Value( "${factorial-api.api-key}")
    private String apiKey; // TODO: remove when oauth flow is available

    @Autowired
    public EmployeesClient(RestClient baseClient, AuthManager authManager) {
        this.baseClient = baseClient;
        this.authManager = authManager;
    }

    public Employee getEmployee(String name) {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        final ApiPaginatedResponse<List<Employee>> response = baseClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH)
                        .queryParam(ONLY_ACTIVE, true)
                        .queryParam(ONLY_MANAGERS, false)
                        .queryParam(FULL_TEXT_NAME, name)
                        .build())
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response) || Objects.isNull(response.data())) {
            throw new EmployeeNotFoundException("Unable to find an Employee for name " + name);
        }
        return response.data().stream()
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("Unable to find an Employee for name " + name));
    }

}
