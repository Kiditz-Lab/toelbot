package com.toelbox.chatbot.facebook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class FacebookService {

	private final FacebookClient facebookClient;
	private final FacebookPageAccessRepository repository;
	private final ApplicationEventPublisher publisher;


	private final FacebookConfigProp prop;

	Facebook.TokenResponse exchangeCodeForAccessToken(String code) {
		log.info("Config : {}", prop);
		return facebookClient.getAccessToken(prop.getAppId(), prop.getRedirectUri(), prop.getAppSecret(), code);
	}

	List<Facebook.Page> getUserPages(String userAccessToken) {
		return facebookClient.getUserPages(userAccessToken, "id,name,category,access_token,picture{url}").getData();
	}

	@Transactional
	void subscribePage(Facebook.SavePageRequest request) {
		facebookClient.subscribePageToApp(request.getAccessToken());
		var access = FacebookPage.builder()
				.pageId(request.getPageId())
				.name(request.getName())
				.category(request.getCategory())
				.imageUrl(request.getImageUrl())
				.accessToken(request.getAccessToken())
				.agentId(request.getAgentId())
				.build();
		access = repository.save(access);
		publisher.publishEvent(new FacebookPageCreatedEvent(access.getId(), access.getAgentId()));
	}
}
