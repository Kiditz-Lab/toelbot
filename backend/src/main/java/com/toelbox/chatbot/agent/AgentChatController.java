package com.toelbox.chatbot.agent;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Tag(name = "Agents")
@Slf4j
class AgentChatController {
	private final AgentChatService service;

	@PostMapping("/{id}/chats")
	Flux<String> chat(@PathVariable UUID id, @Valid @RequestBody AgentChat chat, HttpServletRequest request, Principal principal) throws Exception {
		return service.chat(id, chat, request, principal);
	}

	@GetMapping("/models")
	List<Map<String, String>> getModels() {
		return service.aiModels();
	}

}
