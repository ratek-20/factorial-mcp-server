package com.mcp.factorialmcpserver.service.api.credentials;

import com.mcp.factorialmcpserver.model.credentials.EmployeeCredentials;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.ApiPaginatedResponse;
import com.mcp.factorialmcpserver.service.exception.CurrentEmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
public class CredentialsClient {

    private final RestClient baseClient;
    private final AuthManager authManager;
    private static final String CREDENTIALS_PATH = "/resources/api_public/credentials";

    @Autowired
    public CredentialsClient(RestClient baseClient, AuthManager authManager) {
        this.baseClient = baseClient;
        this.authManager = authManager;
    }

    public EmployeeCredentials getCurrentEmployee() {
        final String accessToken = authManager.getValidAccessToken();
        final ApiPaginatedResponse<List<EmployeeCredentials>> response = baseClient.get()
                .uri(CREDENTIALS_PATH)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (Objects.isNull(response)) {
            throw new CurrentEmployeeNotFoundException("Current employee credentials not found");
        }
        final List<EmployeeCredentials> employeeCredentials = response.data();
        if (Objects.isNull(employeeCredentials) || employeeCredentials.isEmpty()) {
            throw new CurrentEmployeeNotFoundException("Current employee credentials not found");
        }
        return employeeCredentials.getFirst();
    }
}
