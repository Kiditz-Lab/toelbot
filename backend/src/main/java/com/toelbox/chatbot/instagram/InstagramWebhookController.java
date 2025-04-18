package com.toelbox.chatbot.instagram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/instagram/webhook")
@Tag(name = "Instagram")
@RequiredArgsConstructor
class InstagramWebhookController {

	private final InstagramConfigProp prop;
	private final ObjectMapper mapper;
	private final InstagramChatService service;

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
	ResponseEntity<Void> receiveMessage(@RequestBody String payload) throws JsonProcessingException {
		System.out.println("Incoming webhook: " + payload);
		Instagram.InstagramWebhookPayload webhookPayload = mapper.readValue(payload, Instagram.InstagramWebhookPayload.class);
		service.receiveMessage(webhookPayload);
		return ResponseEntity.ok().build();
	}

}
