package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
class TrainingIntegrationDeleteConfig {
	private final TrainingDataRepository repository;
	private final VectorStore vectorStore;
	private final SimpMessagingTemplate template;

	@Bean
	MessageChannel deleteTrainingInputChannel() {
		return new DirectChannel();
	}

	@Bean
	MessageChannel deletePdfChannel() {
		return new DirectChannel();
	}

	@Bean
	MessageChannel deleteTxtChannel() {
		return new DirectChannel();
	}

	@Bean
	MessageChannel deleteWordChannel() {
		return new DirectChannel();
	}

	@Bean
	IntegrationFlow flowDeleteTrainingData() {
		return IntegrationFlow.from("deleteTrainingInputChannel")
				.route(TrainingData.class, this::fileTypeRouter)
				.get();
	}

	private String fileTypeRouter(TrainingData data) {
		String fileName = data.getFile().toLowerCase();
		if (fileName.endsWith(".pdf")) return "deletePdfChannel";
		if (fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".html")) return "deleteWordChannel";
		if (fileName.endsWith(".md")) return "deleteWordChannel";
		if (fileName.endsWith(".txt")) return "deleteTxtChannel";
		return "unknownChannel";
	}

	@Bean
	@ServiceActivator(inputChannel = "deleteTxtChannel")
	@Transactional
	MessageHandler processDeleteTxt() {
		return message -> {
			TrainingData data = (TrainingData) message.getPayload();
			System.out.println("Delete TXT training data: " + data.getUrl());
			var jdbc = (JdbcTemplate) vectorStore.getNativeClient().orElse(null);
			String sql = "DELETE FROM vector_store WHERE metadata->>'source' = ? AND metadata->>'agentId' = ?";
			assert jdbc != null;
			jdbc.update(sql, data.getFile(), data.getAgentId().toString());
			if (data.getType() != TrainingType.TEXT) {
				repository.deleteById(data.getId());
				template.convertAndSend("/topic/training-delete/%s".formatted(data.getAgentId()), Map.of("status", "deleted"));
			}
		};
	}

	@Bean
	@ServiceActivator(inputChannel = "deletePdfChannel")
	@Transactional
	MessageHandler processDeletePdf() {
		return message -> {
			TrainingData data = (TrainingData) message.getPayload();
			System.out.println("Delete PDF training data: " + data.getUrl());
			var jdbc = (JdbcTemplate) vectorStore.getNativeClient().orElse(null);
			String sql = "DELETE FROM vector_store WHERE metadata->>'file_name' = ? AND metadata->>'agentId' = ?";
			assert jdbc != null;
			jdbc.update(sql, data.getFile(), data.getAgentId().toString());
			// NO need to check type since it's only supported for PDF and not TXT
			repository.deleteById(data.getId());
			template.convertAndSend("/topic/training-delete/%s".formatted(data.getAgentId()), Map.of("status", "deleted"));
		};
	}

	@Bean
	@ServiceActivator(inputChannel = "deleteWordChannel")
	@Transactional
	MessageHandler processDeleteWord() {
		return message -> {
			TrainingData data = (TrainingData) message.getPayload();
			System.out.println("Delete Word training data: " + data.getUrl());
			var jdbc = (JdbcTemplate) vectorStore.getNativeClient().orElse(null);
			String sql = "DELETE FROM vector_store WHERE metadata->>'file_name' = ? AND metadata->>'agentId' = ?";
			assert jdbc != null;
			jdbc.update(sql, data.getFile(), data.getAgentId().toString());
			template.convertAndSend("/topic/training-delete/%s".formatted(data.getAgentId()), Map.of("status", "deleted"));
		};
	}


}
