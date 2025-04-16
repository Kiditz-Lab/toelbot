package com.toelbox.chatbot.facebook;

import com.toelbox.chatbot.core.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
	List<FacebookPage> getUserPages(String userAccessToken, String agentId) {
		log.info("Access token: {}", userAccessToken);
		var pages = facebookClient.getUserPages(userAccessToken, "id,name,category,access_token,picture{url}").getData();
		var facebookPages = pages.stream().map(page -> FacebookPage.builder()
				.pageId(page.getId())
				.category(page.getCategory())
				.name(page.getName())
				.agentId(UUID.fromString(agentId))
				.accessToken(page.getAccess_token())
				.imageUrl(page.getPicture().getData().getUrl())
				.build()).toList();
		return repository.saveAll(facebookPages);
	}

	List<FacebookPage> findByAgentId(UUID agentId){
		return repository.findByAgentId(agentId);
	}

	@Transactional
	void subscribePage(Facebook.SavePageRequest request) {
		Map<String, Object> body = Map.of("subscribed_fields", List.of("messages", "message_deliveries", "message_reads", "messaging_postbacks"));
		facebookClient.subscribePageToApp(request.getAccessToken(), body);
		var page = repository.findByPageId(request.getPageId()).orElseThrow(() -> new NotFoundException("Page not found"));

//		var page = FacebookPage.builder()
//				.pageId(request.getPageId())
//				.name(request.getName())
//				.category(request.getCategory())
//				.imageUrl(request.getImageUrl())
//				.accessToken(request.getAccessToken())
//				.agentId(request.getAgentId())
//				.build();
//		page = repository.save(page);
		publisher.publishEvent(new FacebookPageCreatedEvent(page.getPageId(), page.getAgentId()));
	}

	@Transactional
	void unsubscribePage(String pageId) {
		var page = repository.findByPageId(pageId).orElse(null);
		if (page != null) {
			facebookClient.unsubscribePageFromApp(page.getAccessToken());
			publisher.publishEvent(new FacebookPageRemovedEvent(page.getPageId(), page.getAgentId()));
			repository.deleteById(page.getId());
		}
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
