package com.mcp.factorialmcpserver.service.api.timeoff;

import com.mcp.factorialmcpserver.model.timeoff.AllowanceStats;
import com.mcp.factorialmcpserver.model.timeoff.Leave;
import com.mcp.factorialmcpserver.model.timeoff.LeaveType;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.ApiPaginatedResponse;
import com.mcp.factorialmcpserver.service.api.timeoff.request.ApproveLeaveRequest;
import com.mcp.factorialmcpserver.service.api.timeoff.request.LeaveRequest;
import com.mcp.factorialmcpserver.service.api.timeoff.request.UpdateLeaveRequest;
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
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeOffClientTest {

    private TimeOffClient timeOffClient;

    @Mock
    private RestClient baseClient;

    @Mock
    private AuthManager authManager;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private UriBuilder uriBuilder;

    private static final String ACCESS_TOKEN = "test-access-token";

    @BeforeEach
    void setUp() {
        timeOffClient = new TimeOffClient(baseClient, authManager);
        lenient().when(authManager.getValidAccessToken()).thenReturn(ACCESS_TOKEN);
    }

    @Test
    void itShouldGetAllowanceStats() {
        Long employeeId = 123L;
        List<AllowanceStats> expectedStats = Instancio.createList(AllowanceStats.class);
        ApiPaginatedResponse<List<AllowanceStats>> response = new ApiPaginatedResponse<>(expectedStats, null);

        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);
        when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);
        
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(requestHeadersSpec.headers(headersCaptor.capture())).thenReturn(requestHeadersSpec);
        
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(response);

        List<AllowanceStats> allowanceStats = timeOffClient.getAllowanceStats(employeeId);

        // Verify URI
        when(uriBuilder.path("/resources/timeoff/allowance_stats")).thenReturn(uriBuilder);
        when(uriBuilder.queryParam("employee_ids[]", employeeId)).thenReturn(uriBuilder);
        when(uriBuilder.build()).thenReturn(URI.create("http://api.example.com"));
        uriFunctionCaptor.getValue().apply(uriBuilder);

        verify(uriBuilder).path("/resources/timeoff/allowance_stats");
        verify(uriBuilder).queryParam("employee_ids[]", employeeId);

        // Verify Headers
        headersCaptor.getValue().accept(httpHeaders);
        verify(httpHeaders).setBearerAuth(ACCESS_TOKEN);

        assertEquals(expectedStats, allowanceStats);
    }

    @Test
    void itShouldRequestLeave() {
        LeaveRequest request = Instancio.create(LeaveRequest.class);

        when(baseClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/resources/timeoff/leaves")).thenReturn(requestBodySpec);
        
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(requestBodySpec.headers(headersCaptor.capture())).thenReturn(requestBodySpec);
        
        when(requestBodySpec.body(request)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        timeOffClient.requestLeave(request);

        headersCaptor.getValue().accept(httpHeaders);
        verify(httpHeaders).setBearerAuth(ACCESS_TOKEN);
        verify(requestBodySpec).body(request);
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void itShouldGetLeaves() {
        Long employeeId = 123L;
        List<Leave> expectedLeaves = Instancio.createList(Leave.class);
        ApiPaginatedResponse<List<Leave>> response = new ApiPaginatedResponse<>(expectedLeaves, null);

        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);
        when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);
        
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(requestHeadersSpec.headers(headersCaptor.capture())).thenReturn(requestHeadersSpec);
        
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(response);

        List<Leave> leaves = timeOffClient.getLeaves(employeeId);

        // Verify URI
        when(uriBuilder.path("/resources/timeoff/leaves")).thenReturn(uriBuilder);
        when(uriBuilder.queryParam("include_deleted_leaves", false)).thenReturn(uriBuilder);
        when(uriBuilder.queryParam("employee_ids[]", employeeId)).thenReturn(uriBuilder);
        when(uriBuilder.queryParam("from", LocalDate.now().minusYears(1).toString())).thenReturn(uriBuilder);
        when(uriBuilder.build()).thenReturn(URI.create("http://api.example.com"));
        uriFunctionCaptor.getValue().apply(uriBuilder);

        verify(uriBuilder).path("/resources/timeoff/leaves");
        verify(uriBuilder).queryParam("include_deleted_leaves", false);
        verify(uriBuilder).queryParam("employee_ids[]", employeeId);
        verify(uriBuilder).queryParam("from", LocalDate.now().minusYears(1).toString());

        headersCaptor.getValue().accept(httpHeaders);
        verify(httpHeaders).setBearerAuth(ACCESS_TOKEN);

        assertEquals(expectedLeaves, leaves);
    }

    @Test
    void itShouldApproveLeave() {
        Long leaveId = 456L;

        when(baseClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/resources/timeoff/leaves/approve")).thenReturn(requestBodySpec);
        
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(requestBodySpec.headers(headersCaptor.capture())).thenReturn(requestBodySpec);
        
        ArgumentCaptor<ApproveLeaveRequest> bodyCaptor = ArgumentCaptor.forClass(ApproveLeaveRequest.class);
        when(requestBodySpec.body(bodyCaptor.capture())).thenReturn(requestBodySpec);
        
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        timeOffClient.approveLeave(leaveId);

        headersCaptor.getValue().accept(httpHeaders);
        verify(httpHeaders).setBearerAuth(ACCESS_TOKEN);
        assertEquals(leaveId, bodyCaptor.getValue().id());
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void itShouldUpdateLeave() {
        Long leaveId = 789L;
        UpdateLeaveRequest request = Instancio.create(UpdateLeaveRequest.class);

        when(baseClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/resources/timeoff/leaves/" + leaveId)).thenReturn(requestBodySpec);
        
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(requestBodySpec.headers(headersCaptor.capture())).thenReturn(requestBodySpec);
        
        when(requestBodySpec.body(request)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        timeOffClient.updateLeave(leaveId, request);

        headersCaptor.getValue().accept(httpHeaders);
        verify(httpHeaders).setBearerAuth(ACCESS_TOKEN);
        verify(requestBodySpec).body(request);
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void itShouldDeleteLeave() {
        Long leaveId = 999L;

        when(baseClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/resources/timeoff/leaves/" + leaveId)).thenReturn(requestHeadersSpec);
        
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(requestHeadersSpec.headers(headersCaptor.capture())).thenReturn(requestHeadersSpec);
        
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        timeOffClient.deleteLeave(leaveId);

        headersCaptor.getValue().accept(httpHeaders);
        verify(httpHeaders).setBearerAuth(ACCESS_TOKEN);
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void itShouldGetLeaveTypesAndCacheThem() {
        List<LeaveType> expectedTypes = Instancio.createList(LeaveType.class);
        ApiPaginatedResponse<List<LeaveType>> response = new ApiPaginatedResponse<>(expectedTypes, null);

        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/resources/timeoff/leave_types")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(response);

        // First call
        List<LeaveType> result1 = timeOffClient.getLeaveTypes();
        // Second call
        List<LeaveType> result2 = timeOffClient.getLeaveTypes();

        assertEquals(expectedTypes, result1);
        assertEquals(expectedTypes, result2);
        
        // Verify API called only once
        verify(baseClient, times(1)).get();
    }

    @Test
    void itShouldReturnEmptyListWhenAllowanceStatsResponseIsNull() {
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

        List<AllowanceStats> result = timeOffClient.getAllowanceStats(123L);

        assertTrue(result.isEmpty());
    }

    @Test
    void itShouldReturnEmptyListWhenLeavesResponseIsNull() {
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

        List<Leave> result = timeOffClient.getLeaves(123L);

        assertTrue(result.isEmpty());
    }

    @Test
    void itShouldReturnEmptyListWhenLeaveTypesResponseIsNull() {
        when(baseClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

        List<LeaveType> result = timeOffClient.getLeaveTypes();

        assertTrue(result.isEmpty());
    }
}
