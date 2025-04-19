package com.toelbox.auth_client;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/token/calendar")
public class CalendarTokenController {

    private final OAuth2AuthorizedClientService clientService;

    public CalendarTokenController(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/{userId}")
    public Map<String, ?> getCalendarToken(@PathVariable String userId) {
        String provider = "google-calendar";

        // Assuming that the email is used as the principal identifier for the user
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(provider, userId);

        if (client == null) {
            throw new RuntimeException("No Calendar token found for user with email: " + userId);
        }

        OAuth2AccessToken accessToken = client.getAccessToken();

        return Map.of(
                "access_token", accessToken.getTokenValue(),
                "token_type", accessToken.getTokenType().getValue(),
                "expires_at", Objects.requireNonNull(accessToken.getExpiresAt()),
                "scopes", accessToken.getScopes()
        );
    }

}
