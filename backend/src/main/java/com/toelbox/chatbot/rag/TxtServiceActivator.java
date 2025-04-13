package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
class TxtServiceActivator {
	private final MessageChannel ragDeleteChannel;
	private final VectorStore vectorStore;
	private final TrainingDataRepository repository;
	private final TrainingDataService service;

	@Transactional
	@ServiceActivator(inputChannel = "txtChannel")
	void processTxt(File file) {
		var data = repository.findByFile(file.getName()).orElse(null);
		if (data == null) {
			log.info("No data found for file {}", file);
			return;
		}
		try {
			log.info("📝 TXT File Detected: {}", file.toPath());
			doRagProcess(file, data);
			service.markSuccess(data);
			ragDeleteChannel.send(MessageBuilder.withPayload(new RagDelete(data, file)).build());
		} catch (Exception e) {
			log.error("Exception : ", e);
			service.markFailed(data);
		}
	}

	@Transactional
	private void doRagProcess(File file, TrainingData data) {
		Resource resource = new FileSystemResource(file);
		TextReader reader = new TextReader(resource);
		reader.getCustomMetadata().put("agentId", data.getAgentId());
		List<Document> documents = reader.get();
		var splitter = new TokenTextSplitter();
		var split = splitter.apply(documents);
		vectorStore.accept(split);
	}

}
