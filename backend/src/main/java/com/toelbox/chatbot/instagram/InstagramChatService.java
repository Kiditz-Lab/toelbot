package com.toelbox.chatbot.instagram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class InstagramChatService {
	private final InstagramAccountRepository repository;
	private final InstagramClient instagramClient;
	private final ApplicationEventPublisher publisher;

	void receiveMessage(Instagram.InstagramWebhookPayload payload) {
		for (Instagram.Entry entry : payload.entry()) {
			final InstagramAccount account = repository.findByUserId(entry.id()).orElse(null);
			if (account == null) {
				log.info("Account not found for id {}", entry.id());
				continue;
			}
			for (Instagram.Messaging messaging : entry.messaging()) {
				var senderId = messaging.sender().id();
				var recipientId = messaging.recipient().id();
				var message = messaging.message();
				if (message.text() != null) {
					log.info("Message : {}", message.text());
					publisher.publishEvent(new InstagramIncomingMessageEvent(account.getAgentId(), recipientId, senderId, message.text()));
				}
			}
		}
	}

	@Async
	@EventListener
	void replyMessage(InstagramReplyMessageEvent event) {
		sendReply(event.recipientId(), event.senderId(), event.text());
	}

	public void sendReply(String igId, String userId, String text) {
		final InstagramAccount account = repository.findByUserId(igId).orElse(null);
		if (account == null) {
			log.info("Account not found for : {}", igId);
			return;
		}
		Instagram.InstagramSendMessageRequest request = new Instagram.InstagramSendMessageRequest(
				new Instagram.Recipient(userId),
				new Instagram.Message(null, text)
		);

		instagramClient.sendMessage(
				igId,
				"Bearer " + account.getToken(),
				request
		);
	}

	void sendTyping(String igId, String userId) {
		final InstagramAccount account = repository.findByUserId(igId).orElse(null);
		if (account == null) {
			return;
		}
		Instagram.InstagramTypingRequest request = new Instagram.InstagramTypingRequest(
				new Instagram.Recipient(userId),
				"typing_on"
		);
		instagramClient.sendTyping(
				igId,
				"Bearer " + account.getToken(),
				request
		);
	}


}
