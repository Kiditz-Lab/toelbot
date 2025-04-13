package com.toelbox.chatbot.rag;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/training-data")
@RequiredArgsConstructor
@Tag(name = "Training Data")
class TrainingDataController {
	private final TrainingDataService service;

	@PostMapping(value = "/{agentId}/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	List<TrainingData> uploadFile(@PathVariable UUID agentId, @RequestPart(value = "files") List<MultipartFile> files) throws IOException {
		return service.uploadFile(agentId, files);
	}

	@GetMapping("/{agentId}")
	Page<TrainingData> filter(@PathVariable UUID agentId, @RequestParam TrainingType type, @RequestParam(defaultValue = "") String search, @ParameterObject Pageable pageable) {
		return service.findAllByAgentIdAndType(agentId, type, search, pageable);
	}

	@GetMapping("/{agentId}/size")
	TrainingSize getSize(@PathVariable UUID agentId) {
		return service.getSize(agentId);
	}

	@PostMapping("/{agentId}/train-file")
	void trainFile(@PathVariable UUID agentId) {
		service.trainFile(agentId);
	}

	@PostMapping("/train-website")
	void trainWebsite(@RequestBody @Valid @NotEmpty List<UUID> ids) {
		service.trainWebsite(ids);
	}

	@PostMapping(value = "/{agentId}/train-text")
	void trainText(@PathVariable UUID agentId, @Valid @RequestBody TextContent content) throws IOException {
		service.trainText(agentId, content);
	}

	@DeleteMapping("/{trainingDataId}")
	void deleteTrainingData(@PathVariable UUID trainingDataId) {
		service.deleteTrainingData(trainingDataId);
	}

	@DeleteMapping
	void deleteByIds(@RequestParam List<UUID> ids) {
		service.deleteByIds(ids);
	}
}
