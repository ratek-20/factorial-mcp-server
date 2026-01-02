package com.mcp.factorialmcpserver.service.api.authorization.mapper;

import com.mcp.factorialmcpserver.model.auth.AccessToken;
import com.mcp.factorialmcpserver.model.auth.OauthToken;
import com.mcp.factorialmcpserver.service.api.authorization.response.OauthResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenMapper {

    public OauthToken map(OauthResponse oauthResponse) {
        return new OauthToken(buildAccessToken(oauthResponse), oauthResponse.refresh_token());
    }

    private AccessToken buildAccessToken(OauthResponse oauthResponse) {
        return new AccessToken(oauthResponse.access_token(), buildExpiresAt(oauthResponse.expires_in()));
    }

    private Instant buildExpiresAt(Long expiresIn) {
        return Instant.now().plusSeconds(expiresIn);
    }


}
