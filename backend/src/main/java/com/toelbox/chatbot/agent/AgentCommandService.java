package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.NotFoundException;
import com.toelbox.chatbot.core.Timing;
import com.toelbox.chatbot.facebook.FacebookPageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class AgentCommandService {

	private final AgentRepository repository;
	private final AgentChatService chatService;

	Agent createBot(AgentCommand command) {
		boolean isUnique;
		int maxAttempts = 10;
		int attempts = 0;
		String agentKey;
		RandomStringGenerator generator = new RandomStringGenerator.Builder()
				.withinRange('a', 'z').get();

		do {
			if (attempts >= maxAttempts) {
				throw new RuntimeException("Unable to generate unique agentKey after " + maxAttempts + " attempts");
			}
			agentKey = generator.generate(21);
			isUnique = !repository.existsByAgentKey(agentKey);
			attempts++;
		} while (!isUnique);

		var agent = Agent.builder()
				.id(UUID.randomUUID())
				.isPublic(command.isPublic())
				.name(command.name())
				.agentKey(agentKey)
				.config(AgentConfig.builder()
						.aiModel(AIModel.GPT_4O_MINI)
						.prompt("")
						.temperature(0)
						.build())
				.timing(Timing.builder().createdAt(LocalDateTime.now()).build())
				.build();
		return repository.save(agent);
	}

	@Transactional
	Agent addConfig(UUID id, ConfigCommand command) {
		chatService.invalidateCacheByAgentId(id);
		return repository.findById(id).map(agent -> {
			var config = new AgentConfig(command.aiModel(), command.prompt(), command.temperature());
			agent.setConfig(config);
			var model = agent.getTiming();
			model.setUpdatedAt(LocalDateTime.now());
			agent.setTiming(model);
			return repository.save(agent);
		}).orElseThrow(() -> new NotFoundException("Bot not found with ID: " + id));

	}

	Agent updateName(UUID id, AgentCommand command) {
		return repository.findById(id).map(agent -> {
			agent.setName(command.name());
			agent.setPublic(command.isPublic());
			var model = agent.getTiming();
			model.setUpdatedAt(LocalDateTime.now());
			agent.setTiming(model);
			return repository.save(agent);
		}).orElseThrow(() -> new NotFoundException("Bot not found with ID: " + id));
	}

	void deleteAgent(UUID id) {
		chatService.invalidateCacheByAgentId(id);
		repository.deleteById(id);
	}

	@ApplicationModuleListener
	void facebookCreated(FacebookPageCreatedEvent event) {
		log.info("Event received");
		repository.findById(event.agentId()).map(agent -> {
			List<String> facebooks = agent.getFacebooks() == null ? new ArrayList<>() : new ArrayList<>(agent.getFacebooks());
			facebooks.add(event.id().toString());
			agent.setFacebooks(facebooks);
			return repository.save(agent);
		}).orElseThrow(() -> new NotFoundException("Agent not found with ID: " + event.agentId()));
	}
}
