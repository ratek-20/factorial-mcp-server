package com.mcp.factorialmcpserver.service.api.teams;

import com.mcp.factorialmcpserver.model.Team;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.GenericApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TeamsClient {

    private final RestClient baseClient;
    private final AuthManager authManager;

    private static final String BASE_PATH = "/resources/teams/teams";

    @Value( "${factorial-api.api-key}")
    private String apiKey; // TODO: remove when oauth flow is available

    @Autowired
    public TeamsClient(RestClient baseClient, AuthManager authManager) {
        this.baseClient = baseClient;
        this.authManager = authManager;
    }

    public List<Team> getTeams() {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        final GenericApiResponse<List<Team>> response = baseClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH)
                        .build())
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            return List.of();
        }
        return Optional.ofNullable(response.data())
                .orElse(List.of());
    }

    public Team getTeam(Long id) {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        return baseClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH + "/{id}")
                        .build(id))
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                .retrieve()
                .body(Team.class);
    }

}
