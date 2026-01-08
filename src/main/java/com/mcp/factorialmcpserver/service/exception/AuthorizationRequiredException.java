package com.mcp.factorialmcpserver.service.exception;

public class AuthorizationRequiredException extends RuntimeException {

    public AuthorizationRequiredException(String tokenCondition) {
        super(tokenCondition + " access token, please invoke the 'authorize' tool.");
    }

}
