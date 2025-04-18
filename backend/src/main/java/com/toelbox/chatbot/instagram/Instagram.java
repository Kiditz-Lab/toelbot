package com.toelbox.chatbot.instagram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

class Instagram {

	@lombok.Data
	static class TokenResponse {
		@JsonProperty("access_token")
		private String accessToken;
		@JsonProperty("token_type")
		private String tokenType;
		@JsonProperty("expires_in")
		private Long expiresIn;
	}


	@Data
	static class Account {
		@JsonProperty("id")
		private String id;
		@JsonProperty("user_id")
		private String userId;
		@JsonProperty("profile_picture_url")
		private String profilePictureUrl;
		@JsonProperty("name")
		private String name;
		@JsonProperty("username")
		private String username;
	}

	record InstagramSendMessageRequest(
			Recipient recipient,
			Message message
	) {
	}


	record InstagramWebhookPayload(
			String object,
			List<Entry> entry
	) {
	}

	record Entry(
			long time,
			String id,
			List<Messaging> messaging
	) {
	}

	record Messaging(
			Sender sender,
			Recipient recipient,
			long timestamp,
			Message message
	) {
	}

	record Sender(
			String id
	) {
	}

	record Recipient(
			String id
	) {
	}

	record Message(
			String mid,
			String text
	) {
	}

}
