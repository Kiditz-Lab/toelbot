package com.toelbox.chatbot.model;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class ModelService {
	private final ModelRepository modelRepository;

	Iterable<Model> findAll() {
		return modelRepository.findAll(Sort.by("name"));
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
