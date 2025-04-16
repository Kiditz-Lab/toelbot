package com.toelbox.chatbot.instagram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
class InstagramService {
	private final ApiInstagramClient apiInstagramClient;
	private final InstagramClient instagramClient;
	private final InstagramConfigProp config;

	Instagram.TokenResponse exchangeCodeForAccessToken(String code) {
		log.info("CONFIG >> {}", config);
		Map<String, String> form = new HashMap<>();
		form.put("client_id", config.getAppId());
		form.put("client_secret", config.getAppSecret());
		form.put("grant_type", "authorization_code");
		form.put("redirect_uri", config.getRedirectUri());
		form.put("code", code);
		return apiInstagramClient.getAccessToken(form);
	}

	Instagram.Account getAccount(
			String accessToken
	) {
		return instagramClient.getMe("user_id,username,profile_picture_url,name", accessToken);
	}

	@Transactional
	void subscribePage() {
		Map<String, Object> body = Map.of("subscribed_fields", List.of("messages", "message_deliveries", "message_reads", "messaging_postbacks"));
//		publisher.publishEvent(new FacebookPageCreatedEvent(page.getPageId(), page.getAgentId()));
	}


}
