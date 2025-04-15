package com.toelbox.chatbot.facebook;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facebook/webhook")
@Tag(name = "Facebook")
@RequiredArgsConstructor
class FacebookWebhookController {

	private final FacebookConfigProp prop;

	@GetMapping
	ResponseEntity<String> verifyWebhook(
			@RequestParam("hub.mode") String mode,
			@RequestParam("hub.verify_token") String token,
			@RequestParam("hub.challenge") String challenge) {

		if ("subscribe".equals(mode) && prop.getVerifyToken().equals(token)) {
			return ResponseEntity.ok(challenge);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
		}
	}

	@PostMapping
	ResponseEntity<Void> receiveMessage(@RequestBody String payload) {
		System.out.println("Incoming webhook: " + payload);
		return ResponseEntity.ok().build();
	}

}
