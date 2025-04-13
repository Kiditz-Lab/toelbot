package com.toelbox.chatbot.tools;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tools")
@Tag(name = "Tools")
@RequiredArgsConstructor
class ToolsQueryController {
	private final ToolsQueryService queryService;

	@GetMapping
	List<ToolListResponse> filterTools(@RequestParam(required = false, defaultValue = "") String name, @ParameterObject Sort sort) {
		return queryService.filterTools(name, sort);
	}

	@GetMapping("/{id}")
	ToolsResponse getById(@PathVariable UUID id) {
		return queryService.findById(id);
	}


}
