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

	@DeleteMapping("/me/subscribed_apps")
	void unsubscribeFromSubscribedApps(
			@RequestHeader("Authorization") String authorization,
			@RequestParam("subscribed_fields") String subscribedFields
	);

	@PostMapping("/me/subscribed_apps")
	void subscribeFromSubscribedApps(
			@RequestHeader("Authorization") String authorization,
			@RequestParam("subscribed_fields") String subscribedFields
	);

}
