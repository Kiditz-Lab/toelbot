package com.toelbox.chatbot.facebook;

import lombok.Data;

import java.util.List;

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
	}

	@Data
	static class SavePageRequest {
		private String pageId;
		private String name;
		private String accessToken;
		private String agentId;
	}

}
