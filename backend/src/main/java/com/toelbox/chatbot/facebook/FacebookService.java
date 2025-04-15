package com.toelbox.chatbot.facebook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

	@Transactional
	List<FacebookPage> getUserPages(String userAccessToken) {
		var pages = facebookClient.getUserPages(userAccessToken, "id,name,category,access_token,picture{url}").getData();
		return pages.stream().map(page -> FacebookPage.builder()
				.pageId(page.getId())
				.category(page.getCategory())
				.name(page.getName())
				.accessToken(page.getAccess_token())
				.imageUrl(page.getPicture().getData().getUrl())
				.build()).toList();
	}

	@Transactional
	void subscribePage(Facebook.SavePageRequest request) {
		facebookClient.subscribePageToApp(request.getAccessToken(), "messages,message_deliveries,message_reads,messaging_postbacks");
		var access = FacebookPage.builder()
				.pageId(request.getPageId())
				.name(request.getName())
				.category(request.getCategory())
				.imageUrl(request.getImageUrl())
				.accessToken(request.getAccessToken())
				.agentId(request.getAgentId())
				.build();
		access = repository.save(access);
		publisher.publishEvent(new FacebookPageCreatedEvent(access.getPageId(), access.getAgentId()));
	}
}
