package com.mcp.factorialmcpserver.service.exception;

public class CurrentEmployeeNotFoundException extends RuntimeException {
    public CurrentEmployeeNotFoundException(String message) {
        super(message);
    }
}
