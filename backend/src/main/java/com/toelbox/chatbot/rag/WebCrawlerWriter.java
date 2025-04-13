package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class WebCrawlerWriter implements ItemWriter<TrainingData> {
	private final TrainingDataRepository repository;
	private final SimpMessagingTemplate template;
	private final String agentId;


	@Override
	public void write(Chunk<? extends TrainingData> chunk) {
		log.info("Writing a chunk of {} training data entries.", chunk.size());
		repository.saveAll(chunk.getItems());
		template.convertAndSend("/topic/crawl-chunk/%s".formatted(agentId), Map.of("status", "success"));
	}
}
