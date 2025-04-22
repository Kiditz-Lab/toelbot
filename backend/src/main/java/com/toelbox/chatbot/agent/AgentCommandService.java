package com.toelbox.chatbot.agent;

import com.toelbox.chatbot.core.ModelVendor;
import com.toelbox.chatbot.core.NotFoundException;
import com.toelbox.chatbot.core.Timing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
						.aiModel("gpt-4o-mini")
						.prompt("")
						.vendor(ModelVendor.OPEN_AI)
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
			var config = new AgentConfig(command.aiModel(), command.vendor(), command.prompt(), command.temperature());
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

//	@ApplicationModuleListener
//	void facebookCreated(FacebookPageCreatedEvent event) {
//		log.info("Event created");
//		repository.findById(event.agentId()).map(agent -> {
//			List<String> facebooks = agent.getFacebooks() == null ? new ArrayList<>() : new ArrayList<>(agent.getFacebooks());
//			facebooks.add(event.pageId());
//			agent.setFacebooks(facebooks);
//			return repository.save(agent);
//		}).orElseThrow(() -> new NotFoundException("Agent not found with ID: " + event.agentId()));
//	}
//
//	@ApplicationModuleListener
//	void facebookRemoved(FacebookPageRemovedEvent event) {
//		log.info("Event removed");
//		repository.findById(event.agentId()).map(agent -> {
//			List<String> facebooks = agent.getFacebooks() == null ? new ArrayList<>() : new ArrayList<>(agent.getFacebooks());
//			facebooks.remove(event.pageId());
//			agent.setFacebooks(facebooks);
//			return repository.save(agent);
//		}).orElseThrow(() -> new NotFoundException("Agent not found with ID: " + event.agentId()));
//	}
}
