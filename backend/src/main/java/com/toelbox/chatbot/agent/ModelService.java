package com.toelbox.chatbot.agent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ModelService {
	private final ModelRepository modelRepository;

	List<Model> findAll() {
		return (List<Model>) modelRepository.findAll();
	}

	Optional<Model> findById(UUID id) {
		return modelRepository.findById(id);
	}

	void save(List<ModelCommand> commands) {
		var models = commands.parallelStream().map(command -> Model.builder()
				.name(command.name())
				.model(command.model())
				.vendor(command.vendor())
				.build()).toList();
		modelRepository.saveAll(models);
	}

	void deleteById(UUID id) {
		modelRepository.deleteById(id);
	}
}
