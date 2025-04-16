package com.toelbox.chatbot.instagram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Instagram")
@Slf4j
class InstagramController {

	private final InstagramService instagramService;
	private final InstagramConfigProp prop;
	private final ObjectMapper mapper;
	private final Cache<String, String> accessTokenCache;

	@GetMapping("/instagram/callback")
	ResponseEntity<String> handleInstagramCallback(@RequestParam String code, @RequestParam(value = "state", required = false) String state) {
		try {
			Instagram.TokenResponse token = instagramService.exchangeCodeForAccessToken(code);
			InstagramAccount account = instagramService.getAndSaveAccount(token.getAccessToken(), state);
			String json = mapper.writeValueAsString(account);
			log.info("Returning pages with IG business account to {}", prop.getTargetOrigin());

			String html = """
					<!DOCTYPE html>
					<html>
					<body>
					<script>
					    const account = %s;
					    window.opener.postMessage({
					        type: "instagram-connected",
					        payload: {
					            status: "success",
					            account: account
					        }
					    }, "%s");
					    setTimeout(() => window.close(), 500);
					</script>
					</body>
					</html>
					""".formatted(json, prop.getTargetOrigin());

			return ResponseEntity.ok()
					.contentType(MediaType.TEXT_HTML)
					.body(html);

		} catch (Exception e) {
			log.error("Instagram callback error: {}", e.getMessage());

			String errorHtml = generateErrorHtml("Instagram connection failed", prop.getTargetOrigin());
			return ResponseEntity.internalServerError()
					.contentType(MediaType.TEXT_HTML)
					.body(errorHtml);
		}
	}

	private String generateErrorHtml(String message, String targetOrigin) {
		return String.format("""
				<!DOCTYPE html>
				<html>
				<body>
				<script>
				    window.opener.postMessage({
				        type: "instagram-connected",
				        payload: {
				            status: "error",
				            message: "%s"
				        }
				    }, "%s");
				    setTimeout(() => window.close(), 500);
				</script>
				</body>
				</html>
				""", message, targetOrigin);
	}

	@PostMapping("/api/v1/instagram/subscribe-page/{userId}")
	ResponseEntity<Void> subscribePage(@PathVariable String userId) {

		return ResponseEntity.ok().build();
	}
}
