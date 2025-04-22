package com.toelbox.chatbot.model;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/models")
@Tag(name = "Models")
@RequiredArgsConstructor
class ModelController {
	private final ModelService modelService;

	@GetMapping
	Iterable<Model> getAll() {
		return modelService.findAll();
	}

	@GetMapping("/{id}")
	ResponseEntity<Model> getById(@PathVariable UUID id) {
		return modelService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	void create(@RequestBody List<ModelCommand> model) {
		modelService.save(model);
	}

	@DeleteMapping("/{id}")
	ResponseEntity<Void> delete(@PathVariable UUID id) {
		if (modelService.findById(id).isPresent()) {
			modelService.deleteById(id);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
