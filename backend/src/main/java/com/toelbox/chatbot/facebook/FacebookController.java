package com.toelbox.chatbot.facebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Facebook")
@Slf4j
class FacebookController {
	private final ObjectMapper mapper;
	private final FacebookService facebookService;
	private final FacebookConfigProp prop;

	@GetMapping("/facebook/callback")
	public ResponseEntity<String> handleCallback(@RequestParam String code, @RequestParam(value = "state", required = false) String state
	) {
		log.info("STATE: {}", state);
		try {
			Facebook.TokenResponse token = facebookService.exchangeCodeForAccessToken(code);
			List<FacebookPage> pages = facebookService.getUserPages(token.getAccess_token(), state);
			String json = mapper.writeValueAsString(pages);

			log.info("Target Origin : {}", prop.getTargetOrigin());
			String html = """
					<!DOCTYPE html>
					<html>
					<body>
					<script>
					    const pages = %s;
					    window.opener.postMessage({
					        type: "facebook-connected",
					        payload: {
					            status: "success",
					            pages: pages
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
			log.error("Unexpected error: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(generateErrorHtml("An unexpected error occurred", prop.getTargetOrigin()));

		}
	}

	String generateErrorHtml(String message, String targetOrigin) {
		return String.format("""
				<!DOCTYPE html>
				<html>
				<body>
				<script>
				    window.opener.postMessage({
				        type: "facebook-connected",
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

	@PostMapping("/api/v1/facebook/subscribe-page")
	ResponseEntity<FacebookPage> subscribePage(@RequestBody Facebook.SavePageRequest req) {
		return ResponseEntity.ok(facebookService.subscribePage(req));
	}

	@DeleteMapping("/api/v1/facebook/unsubscribe-page/{pageId}")
	ResponseEntity<FacebookPage> unsubscribePage(@PathVariable String pageId) {
		return ResponseEntity.ok(facebookService.unsubscribePage(pageId));
	}

	@PostMapping("/api/v1/facebook-pages/{agentId}")
	ResponseEntity<List<FacebookPage>> getPages(@PathVariable UUID agentId) {
		return ResponseEntity.ok(facebookService.findByAgentId(agentId));
	}

}
