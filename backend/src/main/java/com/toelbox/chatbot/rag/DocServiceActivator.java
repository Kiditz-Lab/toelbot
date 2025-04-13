package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
class DocServiceActivator {
	private final MessageChannel ragDeleteChannel;
	private final VectorStore vectorStore;
	private final TrainingDataRepository repository;
	private final TrainingDataService service;

	@Transactional
	@ServiceActivator(inputChannel = "wordChannel")
	void processWord(File file) throws IOException {
		var data = repository.findByFile(file.getName()).orElse(null);
		if (data == null) {
			log.info("📂 No data found for file: {}", file.getName());
			Files.delete(Path.of(file.getAbsolutePath()));
			return;
		}
		try {
			log.info("📥 Processing file: {}", file.getName());
			doRagProcess(file, data);
			service.markSuccess(data);

			log.info("✅ File processed successfully: {}", file.getName());
		} catch (Exception e) {
			log.error("❌ Failed processing file: {}", file.getName(), e);
			service.markFailed(data);
		} finally {
			ragDeleteChannel.send(MessageBuilder.withPayload(new RagDelete(data, file)).build());
		}
	}

	private void doRagProcess(File file, TrainingData data) {
		log.info("🚀 Starting RAG process for {}", data.getUrl());
		Resource resource = new FileSystemResource(file);
		TikaDocumentReader reader = new TikaDocumentReader(resource);
		List<Document> documents = reader.get()
				.stream().peek(document -> document.getMetadata().put("agentId", data.getAgentId())).toList();
		var splitter = new TokenTextSplitter();
		var split = splitter.apply(documents);
		vectorStore.add(split);
		log.info("✅ Finished RAG process for {}", data.getUrl());
	}
}
