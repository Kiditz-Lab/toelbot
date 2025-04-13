package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebCrawlerJobExecutionListener implements JobExecutionListener {
	private final SimpMessagingTemplate template;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("🔹 Job started: ({})({})", jobExecution.getJobInstance().getJobName(), Thread.currentThread());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus().isUnsuccessful()) {
			log.error("❌ Job failed: {}", jobExecution.getJobInstance().getJobName());
		} else {
			log.info("✅ Job finish: {}", jobExecution.getJobInstance().getJobName());
		}
		String agentIdStr = jobExecution.getJobParameters().getString("agentId");
		template.convertAndSend("/topic/crawl-finish/%s".formatted(agentIdStr), Map.of("status", "success"));
	}
}
