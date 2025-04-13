package com.toelbox.chatbot.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
class WebCrawlerProcessor implements ItemProcessor<String, TrainingData> {
	@Value("${rag.input.temporary}")
	private Resource targetDir;


	private final String agentId;

	public WebCrawlerProcessor(String agentId) {
		this.agentId = agentId;
	}

	@Override
	public TrainingData process(@NonNull String url) {
		log.info("Crawling URL: {}", url);
		try {

			String fileName = sanitizeUrl(url);
			return TrainingData.builder()
					.url(url)
					.agentId(UUID.fromString(agentId))
					.content("")
					.mimeType("text/markdown")
					.status(TrainingStatus.PENDING)
					.type(TrainingType.LINK)
					.file(fileName)
					.prefix("")
					.createdAt(LocalDateTime.now())
					.progress(false)
					.size(0)
					.build();
		} catch (Exception e) {
			log.error("Error ", e);
			throw new RuntimeException(e);
		}
	}

	public static String sanitizeUrl(String url) {
		try {

			URI uri = new URI(url);
			String host = uri.getHost();
			String path = uri.getPath();
			String query = uri.getQuery();

			if (host == null) {
				return "invalid-url.md";
			}

			if (path == null || path.equals("/") || path.isEmpty()) {
				path = "index";
			}

			// Append query to path for uniqueness
			if (query != null && !query.isEmpty()) {
				path += "?" + query;
			}

			// Normalize and sanitize
			String sanitized = Normalizer.normalize(host + path, Normalizer.Form.NFD)
					.replaceAll("[^\\p{ASCII}]", "")
					.replaceAll("[^a-zA-Z0-9-_]", "-")
					.replaceAll("-{2,}", "-")
					.toLowerCase();

			// 🚫 Trim leading/trailing dashes
			sanitized = sanitized.replaceAll("^-+|-+$", "");

			// Trim to a reasonable filename length
			if (sanitized.length() > 100) {
				sanitized = sanitized.substring(0, 100);
			}

			return sanitized + ".md";

		} catch (Exception e) {
			return "invalid-url.md";
		}
	}


}
