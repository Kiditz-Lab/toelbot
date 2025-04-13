package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
class PdfServiceActivator {
	private final MessageChannel ragDeleteChannel;
	private final VectorStore vectorStore;
	private final TrainingDataRepository repository;
	private final TrainingDataService service;

	@Transactional
	@ServiceActivator(inputChannel = "pdfChannel")
	void processPdf(File file) {
		var data = repository.findByFile(file.getName()).orElse(null);
		if (data == null) {
			log.info("No data found for file {}", file);
			return;
		}
		try {
			log.info("📝 PDF File Detected: {}", file.toPath());
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
		Resource pdfResource = new FileSystemResource(file);
		var pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
				.withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
						.withNumberOfTopTextLinesToDelete(0)
						.build())
				.withPagesPerDocument(1)
				.build();
		var pagePdfDocumentReader = new PagePdfDocumentReader(pdfResource, pdfDocumentReaderConfig);
		List<Document> documents = pagePdfDocumentReader.get()
				.stream().peek(document -> document.getMetadata().put("agentId", data.getAgentId())).collect(Collectors.toList());

		var splitter = new TokenTextSplitter();
		var split = splitter.apply(documents);
		vectorStore.accept(split);
	}

}
