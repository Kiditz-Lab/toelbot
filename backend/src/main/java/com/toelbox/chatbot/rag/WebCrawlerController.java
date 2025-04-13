package com.toelbox.chatbot.rag;

import com.toelbox.chatbot.core.AsyncJobLauncher;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Crawl")
@RequestMapping("/api/v1/crawl")
@RequiredArgsConstructor
@Slf4j
class WebCrawlerController {
	private final AsyncJobLauncher asyncJobLauncher;
	private final Job webCrawlerJob;

	@GetMapping("{agentId}")
	public ResponseEntity<String> crawlWebsite(@PathVariable UUID agentId, @RequestParam String url) {
		try {
			JobParameters parameters = new JobParametersBuilder()
					.addString("rootUrl", url)
					.addString("agentId", agentId.toString())
					.addLong("time", System.currentTimeMillis())
					.toJobParameters();

			asyncJobLauncher.launchAsync(webCrawlerJob, parameters);

			return ResponseEntity.ok("Crawling started for " + url);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error starting crawl: " + e.getMessage());
		}

	}
}
