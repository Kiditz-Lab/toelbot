package com.toelbox.auth_client;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/token/drive")
public class DriveTokenController {

    private final OAuth2AuthorizedClientService clientService;

    public DriveTokenController(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/{userId}")
    public Map<String, Object> getDriveToken(@PathVariable String userId) {
        String provider = "google-drive";
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(provider, userId);

        if (client == null) {
            throw new RuntimeException("No Drive token found for userId: " + userId);
        }

        OAuth2AccessToken accessToken = client.getAccessToken();

	    assert accessToken.getExpiresAt() != null;
	    return Map.of(
            "access_token", accessToken.getTokenValue(),
            "token_type", accessToken.getTokenType().getValue(),
            "expires_at", accessToken.getExpiresAt().toString(),
            "scopes", accessToken.getScopes()
        );
    }
}
