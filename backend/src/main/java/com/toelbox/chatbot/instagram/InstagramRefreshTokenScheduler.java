package com.toelbox.chatbot.instagram;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
class InstagramRefreshTokenScheduler {
	private final InstagramAccountRepository repository;
	private final InstagramClient client;

	@Async
	@Scheduled(fixedRate = 24 * 60 * 60 * 1000)
	@Transactional
	public void checkAndRefreshTokens() {
		List<InstagramAccount> accounts = repository.findAll();
		for (InstagramAccount account : accounts) {
			if (account.getExpiresAt().isBefore(LocalDateTime.now().plusDays(1))) {
				var token = client.refreshAccessToken("ig_refresh_token", account.getToken());
				account.setToken(token.getAccessToken());
				account.setExpiresAt(LocalDateTime.now().plusSeconds(token.getExpiresIn()));
				repository.save(account);
			}
		}
	}


}
