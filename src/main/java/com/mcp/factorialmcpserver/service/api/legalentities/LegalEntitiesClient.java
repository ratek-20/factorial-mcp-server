package com.mcp.factorialmcpserver.service.api.legalentities;

import com.mcp.factorialmcpserver.model.LegalEntity;
import com.mcp.factorialmcpserver.service.api.authorization.AuthManager;
import com.mcp.factorialmcpserver.service.api.configuration.GenericApiResponse;
import com.mcp.factorialmcpserver.service.exception.LegalEntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LegalEntitiesClient {

    private final RestClient baseClient;
    private final AuthManager authManager;

    private static final String BASE_PATH = "/resources/companies/legal_entities";

    @Value( "${factorial-api.api-key}")
    private String apiKey; // TODO: remove when oauth flow is available

    @Autowired
    public LegalEntitiesClient(RestClient baseClient, AuthManager authManager) {
        this.baseClient = baseClient;
        this.authManager = authManager;
    }

    public Long getCompanyId() {
        // final String accessToken = authManager.getValidAccessToken(); // TODO: enable when oauth flow is available
        final GenericApiResponse<List<LegalEntity>> response = baseClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH)
                        .build())
                //.headers(headers -> headers.setBearerAuth(accessToken))
                .header("x-api-key", apiKey) // TODO: remove when oauth flow is available
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (Objects.isNull(response)) {
            throw new LegalEntityNotFoundException("Unable to find company id");
        }
        final List<LegalEntity> legalEntities = response.data();
        if (Objects.isNull(legalEntities) || legalEntities.isEmpty()) {
            throw new LegalEntityNotFoundException("Unable to find company id");
        }
        return legalEntities.getFirst().companyId();
    }

}
