package com.toelbox.chatbot.facebook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
class FacebookService {

	private final FacebookClient facebookClient;
	private final FacebookPageRepository repository;
	private final ApplicationEventPublisher publisher;


	private final FacebookConfigProp prop;

	Facebook.TokenResponse exchangeCodeForAccessToken(String code) {
		log.info("Config : {}", prop);
		return facebookClient.getAccessToken(prop.getAppId(), prop.getRedirectUri(), prop.getAppSecret(), code);
	}

	@Transactional
	List<FacebookPage> getUserPages(String userAccessToken) {
		log.info("Access token: {}", userAccessToken);
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
		Map<String, Object> body = Map.of("subscribed_fields", List.of("messages", "message_deliveries", "message_reads", "messaging_postbacks"));
		facebookClient.subscribePageToApp(request.getAccessToken(), body);
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


	void messageReceived(FacebookWebhookResponse response) {
		log.info("Message received: {}", response);
		for (FacebookWebhookResponse.Entry entry : response.entry()) {
			var page = repository.findByPageId(entry.id()).orElse(null);
			if (page != null) {
				for (FacebookWebhookResponse.Messaging messaging : entry.messaging()) {
					if (messaging.message() != null) {
						publisher.publishEvent(new FacebookIncomingMessageEvent(page.getAgentId(), entry.id(), messaging.sender().id(), messaging.message().text()));
					}
				}
			}
		}
	}

	@Async
	@EventListener
	void replyMessage(FacebookReplyMessageEvent event) {
		var message = new Facebook.Message(new Facebook.Recipient(event.senderId()), new Facebook.MessageContent(event.text()));
		repository.findByPageId(event.pageId()).ifPresent(page -> facebookClient.sendMessage(page.getPageId(), page.getAccessToken(), message));
	}
}
