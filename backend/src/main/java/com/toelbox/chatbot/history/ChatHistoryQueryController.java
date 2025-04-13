package com.toelbox.chatbot.history;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/histories")
@Tag(name = "Chat History")
@RequiredArgsConstructor
class ChatHistoryQueryController {
	private final ChatHistoryQueryService service;

	@GetMapping("/{agentId}")
	public Page<ChatHistory> getHistories(@PathVariable UUID agentId,
	                                      @ParameterObject ChatHistoryFilter filter,
	                                      @ParameterObject @PageableDefault(size = 20, sort = "created_at", direction = Sort.Direction.DESC)  Pageable pageable) {

		return service.findByFilters(agentId, filter, pageable);
	}


}
