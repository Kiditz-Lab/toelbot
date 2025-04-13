package com.toelbox.chatbot.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.FileInboundChannelAdapterSpec;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.springframework.integration.file.FileReadingMessageSource.WatchEventType;

@Configuration
@Slf4j
class RagFileIntegrationConfig {
	@Value("${rag.input.path}")
	private Resource inputDir;

	@Bean
	IntegrationFlow fileReadingFlow() throws IOException {
		return IntegrationFlow.from(fileReadingMessageSource(), e -> e.poller(Pollers.fixedRate(Duration.ofSeconds(10)).maxMessagesPerPoll(5)))
				.route(File.class, this::fileTypeRouter)
				.get();
	}


	private FileInboundChannelAdapterSpec fileReadingMessageSource() throws IOException {
		return Files.inboundAdapter(inputDir.getFile())
				.useWatchService(true)
				.watchEvents(WatchEventType.CREATE, WatchEventType.MODIFY)
				.regexFilter(".*\\.(pdf|doc|md|docx|txt|html)$");
	}

	private String fileTypeRouter(File file) {
		String fileName = file.getName().toLowerCase();
		if (fileName.endsWith(".pdf")) return "pdfChannel";
		if (fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".html")) return "wordChannel";
		if (fileName.endsWith(".txt")) return "txtChannel";
		if (fileName.endsWith(".md")) return "mdChannel";
		return "unknownChannel";
	}

	@Bean
	public TaskExecutor ragTaskExecutor() {
		ThreadFactory factory = Thread.ofVirtual()
				.name("rag-vt-", 1)
				.factory();
		return new TaskExecutorAdapter(Executors.newThreadPerTaskExecutor(factory));
	}

	@Bean
	MessageChannel pdfChannel(TaskExecutor ragTaskExecutor) {
		return new ExecutorChannel(ragTaskExecutor);
	}

	@Bean
	MessageChannel wordChannel(TaskExecutor ragTaskExecutor) {
		return new ExecutorChannel(ragTaskExecutor);
	}


	@Bean
	MessageChannel txtChannel(TaskExecutor ragTaskExecutor) {
		return new ExecutorChannel(ragTaskExecutor);
	}

	@Bean
	MessageChannel mdChannel(TaskExecutor ragTaskExecutor) {
		return new ExecutorChannel(ragTaskExecutor);
	}

	@Bean
	MessageChannel ragDeleteChannel(TaskExecutor ragTaskExecutor) {
		return new ExecutorChannel(ragTaskExecutor);
	}
}
