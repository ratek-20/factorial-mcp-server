package com.mcp.factorialmcpserver.entrypoint.mcp;

import com.mcp.factorialmcpserver.model.EmployeeCredentials;
import com.mcp.factorialmcpserver.service.api.credentials.CredentialsClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CredentialTools {

    private final CredentialsClient credentialsClient;

    @Autowired
    public CredentialTools(CredentialsClient credentialsClient) {
        this.credentialsClient = credentialsClient;
    }

    @McpTool(name = "get_current_employee", description = "Returns the full name and employee id for the current user.")
    public EmployeeCredentials getCurrentEmployee() {
        return credentialsClient.getCurrentEmployee();
    }
}
