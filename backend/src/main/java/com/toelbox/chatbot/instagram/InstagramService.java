package com.toelbox.chatbot.instagram;

import com.toelbox.chatbot.core.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class InstagramService {
	private final ApiInstagramClient apiInstagramClient;
	private final InstagramClient instagramClient;
	private final InstagramConfigProp config;
	private final InstagramAccountRepository repository;
	private final Scheduler scheduler;
//	private final Cache<String, String> accessTokenCache;

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


	InstagramAccount getAndSaveAccount(
			String accessToken,
			String agentId
	) {
		var token = instagramClient.exchangeToken("ig_exchange_token", config.getAppSecret(), accessToken);
		var me = instagramClient.getMe("user_id,username,profile_picture_url,name", token.getAccessToken());
		var account = InstagramAccount
				.builder()
				.userId(me.getUserId())
				.name(me.getName())
				.active(false)
				.expiresAt(LocalDateTime.now().plusSeconds(token.getExpiresIn()))
				.token(token.getAccessToken())
				.agentId(UUID.fromString(agentId))
				.username(me.getUsername())
				.profilePictureUrl(me.getProfilePictureUrl())
				.build();
		return repository.save(account);
	}

	@Transactional
	InstagramAccount subscribe(String userId) {
		var account = repository.findByUserId(userId).orElseThrow(() -> new NotFoundException("User not found"));
		instagramClient.subscribeFromSubscribedApps(userId, "Bearer %s".formatted(account.getToken()), "messages");
		account.setActive(true);
		return repository.save(account);
	}

	@Transactional
	InstagramAccount unsubscribe(String userId) {
		var account = repository.findByUserId(userId).orElseThrow(() -> new NotFoundException("User not found"));
		instagramClient.unsubscribeFromSubscribedApps(userId, "Bearer %s".formatted(account.getToken()), "messages");
		account.setActive(false);
		return repository.save(account);
	}


}
