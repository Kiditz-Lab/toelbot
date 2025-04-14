package com.toelbox.chatbot.facebook;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class FacebookService {

	private final FacebookClient facebookClient;

	private final FacebookConfigProp prop;

	Facebook.TokenResponse exchangeCodeForAccessToken(String code) {
		return facebookClient.getAccessToken(prop.getAppId(), prop.getRedirectUri(), prop.getAppSecret(), code);
	}

	List<Facebook.Page> getUserPages(String userAccessToken) {
		return facebookClient.getUserPages(userAccessToken).getData();
	}

	void subscribePage(String pageAccessToken) {
		facebookClient.subscribePageToApp(pageAccessToken);
	}
}
