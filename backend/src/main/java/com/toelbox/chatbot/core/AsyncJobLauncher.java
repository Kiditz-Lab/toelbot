package com.toelbox.chatbot.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

@Slf4j
public class AsyncJobLauncher {
	private final JobLauncher jobLauncher;

	public AsyncJobLauncher(JobLauncher jobLauncher) {
		this.jobLauncher = jobLauncher;
	}

	public void launchAsync(Job job, JobParameters jobParameters) {
		newVirtualThreadPerTaskExecutor().submit(() -> {
			try {
				jobLauncher.run(job, jobParameters);
			} catch (Exception e) {
				log.error("Exception", e);
			}
		});
	}

}
