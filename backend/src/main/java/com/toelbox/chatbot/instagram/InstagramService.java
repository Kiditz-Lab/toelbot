package com.toelbox.chatbot.instagram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
class InstagramService {
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

		return instagramClient.getAccessToken(form);
	}


}
