package com.mcp.factorialmcpserver.service.api.credentials;

import com.mcp.factorialmcpserver.model.credentials.EmployeeCredentials;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.ApiPaginatedResponse;
import com.mcp.factorialmcpserver.service.exception.CurrentEmployeeNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialsClientTest {

    private CredentialsClient credentialsClient;

    @Mock
    private RestClient baseClient;

    @Mock
    private AuthManager authManager;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private HttpHeaders httpHeaders;

    @BeforeEach
    void setUp() {
        credentialsClient = new CredentialsClient(baseClient, authManager);
    }

    @Test
    void itShouldGetCurrentEmployeeFromApiWhenCacheIsEmpty() {
        String accessToken = "access-token";
        EmployeeCredentials expectedEmployee = Instancio.create(EmployeeCredentials.class);
        ApiPaginatedResponse<List<EmployeeCredentials>> response = new ApiPaginatedResponse<>(
                List.of(expectedEmployee),
                null
        );

        when(authManager.getValidAccessToken()).thenReturn(accessToken);
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/resources/api_public/credentials")).thenReturn(requestHeadersSpec);
        
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(requestHeadersSpec.headers(headersCaptor.capture())).thenReturn(requestHeadersSpec);
        
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<EmployeeCredentials>>>() {}))).thenReturn(response);

        EmployeeCredentials employeeCredentials = credentialsClient.getCurrentEmployee();

        headersCaptor.getValue().accept(httpHeaders);
        verify(httpHeaders).setBearerAuth(accessToken);
        assertEquals(expectedEmployee, employeeCredentials);
        verify(authManager, times(1)).getValidAccessToken();
        verify(baseClient, times(1)).get();
    }

    @Test
    void itShouldReturnCachedEmployeeOnSubsequentCalls() {
        String accessToken = "access-token";
        EmployeeCredentials expectedEmployee = Instancio.create(EmployeeCredentials.class);
        ApiPaginatedResponse<List<EmployeeCredentials>> response = new ApiPaginatedResponse<>(
                List.of(expectedEmployee),
                null
        );

        when(authManager.getValidAccessToken()).thenReturn(accessToken);
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/resources/api_public/credentials")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<EmployeeCredentials>>>() {}))).thenReturn(response);

        // First call
        credentialsClient.getCurrentEmployee();
        // Second call
        EmployeeCredentials result = credentialsClient.getCurrentEmployee();

        assertNotNull(result);
        assertEquals(expectedEmployee, result);
        // Verify API was only called once
        verify(authManager, times(1)).getValidAccessToken();
        verify(baseClient, times(1)).get();
    }

    @Test
    void itShouldThrowExceptionWhenApiResponseIsNull() {
        when(authManager.getValidAccessToken()).thenReturn("token");
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<EmployeeCredentials>>>() {}))).thenReturn(null);

        assertThrows(CurrentEmployeeNotFoundException.class, () -> credentialsClient.getCurrentEmployee());
    }

    @Test
    void itShouldThrowExceptionWhenApiResponseDataIsEmpty() {
        ApiPaginatedResponse<List<EmployeeCredentials>> response = new ApiPaginatedResponse<>(
                Collections.emptyList(),
                null
        );

        when(authManager.getValidAccessToken()).thenReturn("token");
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<EmployeeCredentials>>>() {}))).thenReturn(response);

        assertThrows(CurrentEmployeeNotFoundException.class, () -> credentialsClient.getCurrentEmployee());
    }

    @Test
    void itShouldThrowExceptionWhenApiResponseDataIsNull() {
        ApiPaginatedResponse<List<EmployeeCredentials>> response = new ApiPaginatedResponse<>(
                null,
                null
        );

        when(authManager.getValidAccessToken()).thenReturn("token");
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<EmployeeCredentials>>>() {}))).thenReturn(response);

        assertThrows(CurrentEmployeeNotFoundException.class, () -> credentialsClient.getCurrentEmployee());
    }
}
