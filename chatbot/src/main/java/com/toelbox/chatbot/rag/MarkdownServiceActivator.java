package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
class MarkdownServiceActivator {
	private final MessageChannel ragDeleteChannel;
	private final VectorStore vectorStore;
	private final TrainingDataRepository repository;
	private final TrainingDataService service;

	@Transactional
	@ServiceActivator(inputChannel = "mdChannel")
	void processMarkdown(File file) {
		var data = repository.findByFile(file.getName()).orElse(null);
		if (data == null) {
			log.info("No data found for file {}", file);
			return;
		}
		try {
			log.info("📝 Markdown File Detected: {}", file.toPath());
			doRagProcess(file, data);
			ragDeleteChannel.send(MessageBuilder.withPayload(new RagDelete(data, file)).build());
			service.markSuccess(data);
		} catch (Exception e) {
			log.error("Exception : ", e);
			service.markFailed(data);
		}
	}

	@Transactional
	private void doRagProcess(File file, TrainingData data) {
		Resource resource = new FileSystemResource(file);
		MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
				.withHorizontalRuleCreateDocument(true)
				.withIncludeCodeBlock(true)
				.withIncludeBlockquote(true)
				.withAdditionalMetadata(Map.of("agentId", data.getAgentId(), "file_name", file.getName()))
				.build();
		MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
		var documents = reader.get();
		var splitter = new TokenTextSplitter();
		var split = splitter.apply(documents);
		vectorStore.accept(split);
	}

}
