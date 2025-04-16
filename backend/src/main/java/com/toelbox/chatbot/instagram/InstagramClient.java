package com.toelbox.chatbot.instagram;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "instagramClient", url = "https://graph.instagram.com/v22.0", configuration = FeignConfig.class)
interface InstagramClient {

	@GetMapping("/me")
	Instagram.Account getMe(
			@RequestParam("fields") String fields,
			@RequestParam("access_token") String accessToken
	);

	@GetMapping("/access_token")
	Instagram.TokenResponse exchangeToken(
			@RequestParam("grant_type") String grantType,
			@RequestParam("client_secret") String clientSecret,
			@RequestParam("access_token") String accessToken
	);

	@GetMapping("/refresh_access_token")
	Instagram.TokenResponse refreshAccessToken(
			@RequestParam("grant_type") String grantType,
			@RequestParam("access_token") String accessToken
	);


	@DeleteMapping("/{userId}/subscribed_apps")
	void unsubscribeFromSubscribedApps(
			@PathVariable String userId,
			@RequestHeader("Authorization") String authorization,
			@RequestParam("subscribed_fields") String subscribedFields
	);

	@PostMapping("/{userId}/subscribed_apps")
	void subscribeFromSubscribedApps(
			@PathVariable String userId,
			@RequestHeader("Authorization") String authorization,
			@RequestParam("subscribed_fields") String subscribedFields
	);

}
