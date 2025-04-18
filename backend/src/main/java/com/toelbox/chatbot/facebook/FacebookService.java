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
	public List<FacebookPage> getUserPages(String userAccessToken, String agentId) {
		log.info("Access token: {}", userAccessToken);
		var pages = facebookClient.getUserPages(userAccessToken, "id,name,category,access_token,picture{url}").getData();
		UUID agentUUID = UUID.fromString(agentId);

		List<FacebookPage> facebookPages = pages.stream().map(page -> {
			FacebookPage existing = repository.findByPageId(page.getId()).orElse(null);

			if (existing != null) {
				// Update existing record
				existing.setCategory(page.getCategory());
				existing.setName(page.getName());
				existing.setAgentId(agentUUID);
				existing.setAccessToken(page.getAccess_token());
				existing.setImageUrl(page.getPicture().getData().getUrl());
				return existing;
			} else {
				// Create new record
				return FacebookPage.builder()
						.pageId(page.getId())
						.category(page.getCategory())
						.name(page.getName())
						.agentId(agentUUID)
						.accessToken(page.getAccess_token())
						.imageUrl(page.getPicture().getData().getUrl())
						.build();
			}
		}).toList();

		return repository.saveAll(facebookPages);
	}


	List<FacebookPage> findByAgentId(UUID agentId) {
		return repository.findByAgentId(agentId);
	}

	@Transactional
	FacebookPage subscribePage(Facebook.SavePageRequest request) {
		Map<String, Object> body = Map.of("subscribed_fields", List.of("messages", "message_deliveries", "message_reads", "messaging_postbacks"));
		facebookClient.subscribePageToApp(request.getAccessToken(), body);
		var page = repository.findByPageIdForUpdate(request.getPageId()).orElseThrow(() -> new NotFoundException("Page not found"));
		page.setActive(true);
//		publisher.publishEvent(new FacebookPageCreatedEvent(page.getPageId(), page.getAgentId()));
		return repository.save(page);
	}

	@Transactional
	FacebookPage unsubscribePage(String pageId) {
		var page = repository.findByPageIdForUpdate(pageId).orElseThrow(() -> new NotFoundException("Page not found"));
		page.setActive(false);
		facebookClient.unsubscribePageFromApp(page.getAccessToken());
		return repository.save(page);
	}

	void messageReceived(FacebookWebhookPayload response) {
		log.info("Message received: {}", response);
		for (FacebookWebhookPayload.Entry entry : response.entry()) {
			var page = repository.findByPageId(entry.id()).orElse(null);
			if (page != null) {
				for (FacebookWebhookPayload.Messaging messaging : entry.messaging()) {
					if (messaging.message() != null) {
						Map<String, Object> body = Map.of(
								"recipient", Map.of("id", messaging.sender().id()),
								"sender_action", "typing_on"
						);
						facebookClient.sendTypingIndicator(page.getAccessToken(), body);
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
