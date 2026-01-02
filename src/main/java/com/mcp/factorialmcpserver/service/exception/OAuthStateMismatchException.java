package com.mcp.factorialmcpserver.service.exception;

public class OAuthStateMismatchException extends RuntimeException {

    public OAuthStateMismatchException(String message) {
        super(message);
    }

}
