package com.toelbox.chatbot.rag;

import com.toelbox.chatbot.core.AsyncJobLauncher;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
class WebCrawlerConfig {
	private final PlatformTransactionManager transactionManager;
	private final TrainingDataRepository repository;
	private final JobLauncher jobLauncher;
	private final WebCrawlerJobExecutionListener listener;

	@Bean
	public Job webCrawlerJob(JobRepository jobRepository, Step webCrawlerStep) {
		return new JobBuilder("webCrawlerJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(webCrawlerStep)
				.listener(listener)
				.build();
	}

	@Bean
	public Step webCrawlerStep(JobRepository jobRepository, WebCrawlerReader reader, WebCrawlerProcessor processor, WebCrawlerWriter writer, TaskExecutor webCrawlerTaskExecutor) {
		return new StepBuilder("webCrawlerStep", jobRepository)
				.<String, TrainingData>chunk(20, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.taskExecutor(webCrawlerTaskExecutor)
				.build();
	}

	@Bean
	@StepScope
	WebCrawlerReader urlReader(@Value("#{jobParameters['rootUrl']}") String rootUrl) {
		return new WebCrawlerReader(rootUrl);
	}

	@Bean
	@StepScope
	WebCrawlerProcessor webCrawlerProcessor(@Value("#{jobParameters['agentId']}") String agentId) {
		return new WebCrawlerProcessor(agentId);
	}


	@Bean
	@StepScope
	WebCrawlerWriter webCrawlerItemWriter(@Value("#{jobParameters['agentId']}") String agentId, SimpMessagingTemplate template) {
		return new WebCrawlerWriter(repository, template, agentId);
	}

	@Bean
	public TaskExecutor webCrawlerTaskExecutor() {
		return new ConcurrentTaskExecutor(Executors.newVirtualThreadPerTaskExecutor());
	}

	@Bean
	public AsyncJobLauncher asyncJobLauncher() {
		return new AsyncJobLauncher(jobLauncher);
	}


}
