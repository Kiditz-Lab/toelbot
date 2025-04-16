package com.toelbox.chatbot.instagram;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class TokenRefreshJob extends QuartzJobBean {
	@Override
	protected void executeInternal(JobExecutionContext context) {

	}
}
