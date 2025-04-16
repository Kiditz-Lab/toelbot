package com.toelbox.chatbot.instagram;

class Instagram {

	@lombok.Data
	static class TokenResponse {
		private String access_token;
		private String token_type;
		private Long expires_in;
	}


	@lombok.Data
	static class IgBusinessProfile {
		private String id;
		private String username;
		private String name;
		private String profile_picture_url;
		private String biography;
	}
}
