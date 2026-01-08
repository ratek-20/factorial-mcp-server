package com.mcp.factorialmcpserver.service.exception;

public class AuthenticationRequiredException extends RuntimeException {

    public AuthenticationRequiredException(String tokenCondition) {
        super(tokenCondition + " access token, please invoke the 'authenticate' tool.");
    }

}
