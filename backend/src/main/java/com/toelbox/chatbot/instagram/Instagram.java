package com.toelbox.chatbot.instagram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
}
