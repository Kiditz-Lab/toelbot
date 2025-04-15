package com.toelbox.chatbot.facebook;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facebookClient", url = "https://graph.facebook.com/v18.0")
interface FacebookClient {
	@GetMapping("/oauth/access_token")
	Facebook.TokenResponse getAccessToken(
			@RequestParam("client_id") String clientId,
			@RequestParam("redirect_uri") String redirectUri,
			@RequestParam("client_secret") String clientSecret,
			@RequestParam("code") String code
	);

	@GetMapping("/me/accounts")
	Facebook.PagesResponse getUserPages(@RequestParam("access_token") String userAccessToken, @RequestParam("fields") String fields);

	@PostMapping("/me/subscribed_apps")
	void subscribePageToApp(@RequestParam("access_token") String pageAccessToken, @RequestParam("subscribed_fields") String subscribedFields);


}
