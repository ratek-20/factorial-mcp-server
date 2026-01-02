package com.mcp.factorialmcpserver.entrypoint.http;

import com.mcp.factorialmcpserver.service.api.AuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthManager authManager;

    @Autowired
    public AuthController(AuthManager authManager) {
        this.authManager = authManager;
    }

    @GetMapping(value = "/oauth2-callback", produces = MediaType.TEXT_HTML_VALUE)
    public String callback(@RequestParam String code, @RequestParam String state) {
        authManager.handleCallback(code, state);
        return """
                  <html><body style="font-family: sans-serif">
                    <h2>✅ Auth completed</h2>
                    <p>You can close this window.</p>
                  </body></html>
                """;
    }

}
