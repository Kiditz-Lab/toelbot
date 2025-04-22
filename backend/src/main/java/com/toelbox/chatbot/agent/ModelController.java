package com.toelbox.chatbot.agent;

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
	List<Model> getAll() {
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

//	@PutMapping("/{id}")
//	ResponseEntity<Model> update(@PathVariable UUID id, @RequestBody Model model) {
//		return modelService.findById(id)
//				.map(existing -> {
//					model.setId(id);
//					return ResponseEntity.ok(modelService.save(model));
//				})
//				.orElse(ResponseEntity.notFound().build());
//	}

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
