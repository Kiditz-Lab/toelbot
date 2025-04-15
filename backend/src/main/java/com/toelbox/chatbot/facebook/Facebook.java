package com.toelbox.chatbot.facebook;

import lombok.Data;

import java.util.List;
import java.util.UUID;

class Facebook {
	@Data
	static class TokenResponse {
		private String access_token;
		private String token_type;
		private Long expires_in;
	}

	@Data
	static class PagesResponse {
		private List<Page> data;
	}

	@Data
	static class Page {
		private String id;
		private String name;
		private String category;
		private String access_token;
		private Picture picture;
	}

	@Data
	static class Picture {
		private PictureData data;
	}

	@Data
	static class PictureData {
		private String url;
	}

	@Data
	static class SavePageRequest {
		private String pageId;
		private String name;
		private String category;
		private String imageUrl;
		private String accessToken;
		private UUID agentId;
	}

	record Message(Recipient recipient, MessageContent message) {
	}

	record Recipient(String id) {
	}

	record MessageContent(String text) {
	}


}
