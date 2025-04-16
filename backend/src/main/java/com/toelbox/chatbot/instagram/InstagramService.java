package com.toelbox.chatbot.instagram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
 class InstagramService {
    private final InstagramClient instagramClient;
    private final InstagramConfigProp config;

    public Instagram.TokenResponse exchangeCodeForAccessToken(String code) {
        String clientId = config.getAppId();
        String clientSecret = config.getAppSecret();
        String redirectUri = "https://api.toelbox.com/instagram/callback"; // Do NOT encode manually

        String url = String.format(
                "https://graph.facebook.com/v18.0/oauth/access_token" +
                        "?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s",
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(clientSecret, StandardCharsets.UTF_8),
                URLEncoder.encode(code, StandardCharsets.UTF_8)
        );

        ResponseEntity<Instagram.TokenResponse> response = new RestTemplate().getForEntity(url, Instagram.TokenResponse.class);
        return response.getBody();
    }



    public List<Instagram.FacebookPage> getFacebookPages(String accessToken) {
        log.info("Access Token : {}", accessToken);
        return instagramClient.getFacebookPages(accessToken).getData();
    }


    Optional<Instagram.PageDetails> getPageDetailsWithInstagram(String pageId, String accessToken) {
        try {
            Instagram.PageDetails page = instagramClient.getPageDetails(
                    pageId,
                    "id,name,instagram_business_account",
                    accessToken
            );
            return Optional.of(page);
        } catch (Exception e) {
            log.warn("Could not fetch details for page: {}", pageId, e);
            return Optional.empty();
        }
    }

    Optional<Instagram.IgBusinessProfile> getInstagramProfile(String igBusinessId, String accessToken) {
        try {
            Instagram.IgBusinessProfile profile = instagramClient.getInstagramAccountDetails(
                    igBusinessId,
                    "id,username,profile_picture_url",
                    accessToken
            );
            return Optional.of(profile);
        } catch (Exception e) {
            log.warn("Could not fetch IG business profile: {}", igBusinessId, e);
            return Optional.empty();
        }
    }


}
