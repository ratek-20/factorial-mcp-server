package com.mcp.factorialmcpserver.service.api.employees;

import com.mcp.factorialmcpserver.model.employees.Employee;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.ApiPaginatedResponse;
import com.mcp.factorialmcpserver.service.exception.EmployeeNotFoundException;
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
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeesClientTest {

    private EmployeesClient employeesClient;

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

    @Mock
    private UriBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        employeesClient = new EmployeesClient(baseClient, authManager);
    }

    @Test
    void itShouldGetEmployeeFromApiWhenCacheIsEmpty() {
        String employeeName = "John Doe";
        String accessToken = "access-token";
        Employee expectedEmployee = Instancio.create(Employee.class);
        ApiPaginatedResponse<List<Employee>> apiPaginatedResponse = new ApiPaginatedResponse<>(
                List.of(expectedEmployee),
                null
        );

        when(authManager.getValidAccessToken()).thenReturn(accessToken);
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);

        ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);
        when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);

        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(requestHeadersSpec.headers(headersCaptor.capture())).thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<Employee>>>() {}))).thenReturn(apiPaginatedResponse);

        Employee employee = employeesClient.getEmployee(employeeName);

        // Verify URI function
        when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
        when(uriBuilder.queryParam(anyString(), any(Object.class))).thenReturn(uriBuilder);
        when(uriBuilder.build()).thenReturn(URI.create("http://api.example.com"));
        uriFunctionCaptor.getValue().apply(uriBuilder);

        verify(uriBuilder).path("/resources/employees/employees");
        verify(uriBuilder).queryParam("only_active", true);
        verify(uriBuilder).queryParam("only_managers", false);
        verify(uriBuilder).queryParam("full_text_name", employeeName);

        // Verify Headers
        headersCaptor.getValue().accept(httpHeaders);
        verify(httpHeaders).setBearerAuth(accessToken);

        assertEquals(expectedEmployee, employee);
        verify(authManager, times(1)).getValidAccessToken();
        verify(baseClient, times(1)).get();
    }

    @Test
    void itShouldReturnCachedEmployeeOnSubsequentCalls() {
        String employeeName = "John Doe";
        String accessToken = "access-token";
        Employee expectedEmployee = Instancio.create(Employee.class);
        ApiPaginatedResponse<List<Employee>> apiPaginatedResponse = new ApiPaginatedResponse<>(
                List.of(expectedEmployee),
                null
        );

        when(authManager.getValidAccessToken()).thenReturn(accessToken);
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<Employee>>>() {}))).thenReturn(apiPaginatedResponse);

        // First call
        employeesClient.getEmployee(employeeName);
        // Second call
        Employee employee = employeesClient.getEmployee(employeeName);

        assertNotNull(employee);
        assertEquals(expectedEmployee, employee);
        // Verify API was only called once for the same name
        verify(authManager, times(1)).getValidAccessToken();
        verify(baseClient, times(1)).get();
    }

    @Test
    void itShouldThrowExceptionWhenApiResponseIsNull() {
        String employeeName = "Unknown";
        when(authManager.getValidAccessToken()).thenReturn("token");
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<Employee>>>() {}))).thenReturn(null);

        assertThrows(EmployeeNotFoundException.class, () -> employeesClient.getEmployee(employeeName));
    }

    @Test
    void itShouldThrowExceptionWhenApiResponseDataIsNull() {
        String employeeName = "Unknown";
        ApiPaginatedResponse<List<Employee>> apiPaginatedResponse = new ApiPaginatedResponse<>(null, null);

        when(authManager.getValidAccessToken()).thenReturn("token");
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<Employee>>>() {}))).thenReturn(apiPaginatedResponse);

        assertThrows(EmployeeNotFoundException.class, () -> employeesClient.getEmployee(employeeName));
    }

    @Test
    void itShouldThrowExceptionWhenApiResponseDataIsEmpty() {
        String employeeName = "Unknown";
        ApiPaginatedResponse<List<Employee>> apiPaginatedResponse = new ApiPaginatedResponse<>(Collections.emptyList(), null);

        when(authManager.getValidAccessToken()).thenReturn("token");
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(new ParameterizedTypeReference<ApiPaginatedResponse<List<Employee>>>() {}))).thenReturn(apiPaginatedResponse);

        assertThrows(EmployeeNotFoundException.class, () -> employeesClient.getEmployee(employeeName));
    }
}
