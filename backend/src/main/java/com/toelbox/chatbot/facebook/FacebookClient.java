package com.toelbox.chatbot.facebook;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
	void subscribePageToApp(@RequestParam("access_token") String pageAccessToken, @RequestBody Map<String, Object> body);

	@PostMapping("/{pageId}/messages")
	void sendMessage(@PathVariable("pageId") String pageId,
	                 @RequestParam("access_token") String accessToken,
	                 @RequestBody Facebook.Message message);

}

