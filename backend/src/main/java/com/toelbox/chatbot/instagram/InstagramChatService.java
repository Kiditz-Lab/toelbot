package com.toelbox.chatbot.instagram;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class InstagramChatService {
	private final InstagramAccountRepository repository;
	private final InstagramClient instagramClient;

	void receiveMessage(Instagram.InstagramWebhookPayload payload) {
		for (Instagram.Entry entry : payload.entry()) {
			for (Instagram.Messaging messaging : entry.messaging()) {
				var senderId = messaging.sender().id();
				var recipientId = messaging.recipient().id();
				var message = messaging.message();
				if (StringUtils.isNotEmpty(message.text())) {
					sendTyping(recipientId, senderId);
					sendReply(recipientId, senderId, "Thanks for your message: " + message.text());
				}
			}
		}
	}

	public void sendReply(String igId, String userId, String text) {
		final InstagramAccount account = repository.findByUserId(userId).orElse(null);
		if (account == null) {
			return;
		}
		Instagram.InstagramSendMessageRequest request = new Instagram.InstagramSendMessageRequest(
				new Instagram.Recipient(userId),
				new Instagram.Message(null, text)
		);

		instagramClient.sendMessage(
				igId,
				"Bearer " + account.getToken(),
				"application/json",
				request
		);
	}

	void sendTyping(String igId, String userId) {
		final InstagramAccount account = repository.findByUserId(userId).orElse(null);
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
				"application/json",
				request
		);
	}


}
