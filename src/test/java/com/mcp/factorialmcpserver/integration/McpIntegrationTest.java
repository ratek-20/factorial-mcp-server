package com.mcp.factorialmcpserver.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "OAUTH2_APPLICATION_ID=dummy-id",
        "OAUTH2_APPLICATION_SECRET=dummy-secret"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class McpIntegrationTest {

    /*
    @DirtiesContext indicates that the application context should be closed and removed from the cache
    after the test class has finished.
    This is important because the test modifies System.in and System.out globally.
     */

    static WireMockServer wireMockServer;

    private static PipedOutputStream clientToServer;
    private static PipedInputStream serverStdin;

    private static PipedOutputStream serverToClient;
    private static PipedInputStream clientStdout;


    /*
    Replaces a bean with a mock in the spring application context.
    Mocked to avoid dealing with real OAuth2 flows during the test, this test is focused on the mcp communication.
     */
    @MockitoBean
    private AuthManager authManager;

    /*
    Allows adding properties to the Spring Environment dynamically.
    It is executed very early in the Spring Boot lifecycle, before the ApplicationContext
    is fully refreshed and before any beans are instantiated.
     */
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) throws Exception {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        /*
        Establish a piped stream chain to simulate STDIO communication.
        What is written to clientToServer can be read from serverStdin,
        and what the server writes to serverToClient can be read from clientStdout.
         */
        clientToServer = new PipedOutputStream();
        serverStdin = new PipedInputStream(clientToServer);

        serverToClient = new PipedOutputStream();
        clientStdout = new PipedInputStream(serverToClient);

        // Hijack System.in and System.out to redirect server communication through the pipes.
        System.setIn(serverStdin);
        System.setOut(new PrintStream(serverToClient, true));

        /*
        Override the remote API URL properties to point to the local WireMock server.
        This ensures that HTTP calls made by the application are intercepted by the mock.
         */
        registry.add("factorial-api.base-url", () -> "http://localhost:" + wireMockServer.port() + "/");
        registry.add("factorial-api.version", () -> "");
    }

    @AfterAll
    static void afterAll() throws Exception {
        wireMockServer.stop();
        clientToServer.close();
        serverStdin.close();
        serverToClient.close();
        clientStdout.close();
    }

    @Test
    void itShouldGetTheCurrentEmployee() throws Exception {
        when(authManager.getValidAccessToken()).thenReturn("fake-token");

        wireMockServer.stubFor(get(urlEqualTo("/resources/api_public/credentials"))
                .willReturn(okJson("""
                  {
                    "data": [
                      {
                        "full_name": "Alice",
                        "employee_id": 24
                      }
                    ],
                    "meta": {}
                  }
                """)));

        try (var client = new McpClient(clientStdout, clientToServer, Duration.ofSeconds(2))) {
            client.initialize();

            String getCurrentEmployeeTooName = "get_current_employee";

            JsonNode toolsExposedByTheServer = client.listTools();
            assertThat(toolsExposedByTheServer.toString()).contains(getCurrentEmployeeTooName);

            JsonNode currentEmployee = client.callTool(getCurrentEmployeeTooName);
            assertThat(currentEmployee.toString()).contains("Alice");
        }

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/resources/api_public/credentials")));
    }
}

