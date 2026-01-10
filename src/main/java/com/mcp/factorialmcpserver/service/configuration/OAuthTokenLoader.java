package com.mcp.factorialmcpserver.service.configuration;

import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.store.OAuthTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class OAuthTokenLoader implements ApplicationRunner {

    private final OAuthTokenStore tokenStore;
    private final AuthManager authManager;

    @Autowired
    public OAuthTokenLoader(OAuthTokenStore tokenStore, AuthManager authManager) {
        this.tokenStore = tokenStore;
        this.authManager = authManager;
    }

    @Override
    public void run(ApplicationArguments args) {
        tokenStore.load()
                .ifPresent(authManager::setOAuthToken);
    }
}
