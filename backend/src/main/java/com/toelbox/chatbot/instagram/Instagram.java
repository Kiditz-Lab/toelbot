package com.toelbox.chatbot.instagram;

import java.util.List;

class Instagram {

	@lombok.Data
	static class TokenResponse {
		private String access_token;
		private String token_type;
		private Long expires_in;
	}

	@lombok.Data
	static class FacebookAccountsResponse {
		private List<FacebookPage> data;
	}

	@lombok.Data
	static class FacebookPage {
		private String id;
		private String name;
		private InstagramBusinessAccount instagram_business_account;
	}

	@lombok.Data
	static class InstagramBusinessAccount {
		private String id; // This is the IG business ID
	}

	@lombok.Data
	static class PageDetails {
		private InstagramBusinessAccount instagram_business_account;
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
