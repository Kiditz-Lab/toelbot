package com.toelbox.chatbot.rag;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@Slf4j
@RequiredArgsConstructor
class TrainingIntegrationConfig {

	@Value("${rag.input.temporary}")
	private Resource inputDir;
	@Value("${rag.input.path}")
	private Resource ragDir;
	private final TrainingDataRepository repository;
	private final SimpMessagingTemplate template;
	private final VectorStoreService vectorStoreService;
	private final HtmlService htmlService;

	@Bean
	MessageChannel trainingInputChannel(TaskExecutor trainingTaskExecutor) {
		return new ExecutorChannel(trainingTaskExecutor);
	}

	@Bean
	MessageChannel linkTrainingChannel(TaskExecutor trainingTaskExecutor) {
		return new ExecutorChannel(trainingTaskExecutor);
	}

	@Bean
	MessageChannel fileTrainingChannel(TaskExecutor trainingTaskExecutor) {
		return new ExecutorChannel(trainingTaskExecutor);
	}

	@Bean
	IntegrationFlow trainingFlow() {
		return IntegrationFlow
				.from("trainingInputChannel")
				.split()
				.route(TrainingData.class, TrainingData::getType, mapping -> mapping
						.channelMapping(TrainingType.LINK, "linkTrainingChannel")
						.channelMapping(TrainingType.FILE, "fileTrainingChannel")
						.channelMapping(TrainingType.TEXT, "fileTrainingChannel")
				)
				.get();
	}

	@Bean
	@ServiceActivator(inputChannel = "linkTrainingChannel")
	MessageHandler processLinkTraining() {
		return message -> {
			TrainingData data = (TrainingData) message.getPayload();
			log.info("Processing LINK training data: {}", data.getUrl());
			processTrainingLink(data);
		};
	}

	@Bean
	@ServiceActivator(inputChannel = "fileTrainingChannel")
	MessageHandler processFileTraining() {
		return message -> {
			TrainingData data = (TrainingData) message.getPayload();
			log.info("Processing FILE training data: {}", data.getUrl());
			copyTrainingDataFile(data);
		};
	}

	void copyTrainingDataFile(TrainingData training) {
		try {
			Path inputPath = inputDir.getFile().toPath().resolve(training.getAgentId().toString()).resolve(training.getUrl());
			Path targetPath = ragDir.getFile().toPath().resolve("%s_%s".formatted(training.getPrefix(), training.getUrl()));

			// Ensure target directory exists
			Files.createDirectories(targetPath.getParent());

			// Copy file
			Files.move(inputPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			training.setProgress(true);
			repository.save(training);
			template.convertAndSend("/topic/training/%s".formatted(training.getAgentId()), training);
			log.info("File moved successfully: {}", targetPath);
		} catch (IOException e) {
			log.error("Failed to move file: {}", training.getUrl(), e);
		}
	}

	@Transactional
	void processTrainingLink(TrainingData training) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String prefix = formatter.format(LocalDateTime.now());
			vectorStoreService.delete("source", training.getFile(), training.getAgentId());
			String fileName = "%s_%s".formatted(prefix, training.getFile());
			Path filePath = ragDir.getFile().toPath().resolve(fileName);
			Files.createDirectories(filePath.getParent());
			Document document = Jsoup.connect(training.getUrl())
					.timeout(15_000)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
					.referrer("https://www.google.com/")
					.followRedirects(true)
					.get();
			document.select("[href]").forEach(element -> element.attr("href", element.absUrl("href")));
			document.select("[src]").forEach(element -> element.attr("src", element.absUrl("src")));
			var html = document.body().html();
			var content = FlexmarkHtmlConverter.builder().build().convert(html);
			TrainingWebsite website = getSiteInformation(document);
			var bytes = content.getBytes(StandardCharsets.UTF_8);
			training.setStatus(TrainingStatus.PROCESSING);
			training.setSize(bytes.length);
			training.setWebsite(website);
			training.setProgress(true);
			training.setFile(fileName);
			repository.save(training);
			Files.write(filePath, bytes);
			template.convertAndSend("/topic/training/%s".formatted(training.getAgentId()), training);
			log.info("Html File generated successfully: {}", filePath);
		} catch (IOException e) {
			training.setStatus(TrainingStatus.ERROR);
			training.setProgress(false);
			repository.save(training);
			template.convertAndSend("/topic/training/%s".formatted(training.getAgentId()), training);
			log.error("Failed to generate html file: {}", training.getUrl(), e);
		}
	}

	private TrainingWebsite getSiteInformation(Document document) {
		// Extract Document Title
		String title = document.title();

		// Extract description (Open Graph or meta)
		String description = document.select("meta[name=description]").attr("content");
		if (description.isEmpty()) {
			description = document.select("meta[property=og:description]").attr("content");
		}

		// Extract image (Open Graph or link rel image)
		String image = document.select("meta[property=og:image]").attr("content");
		if (image.isEmpty()) {
			image = document.select("link[rel=image_src]").attr("href");
		}
		return new TrainingWebsite(title, description, image);

	}

	@Bean
	public TaskExecutor trainingTaskExecutor() {
		ThreadFactory factory = Thread.ofVirtual()
				.name("training-vt-", 1)
				.factory();
		return new TaskExecutorAdapter(Executors.newThreadPerTaskExecutor(factory));
	}




}
