package com.toelbox.chatbot.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
class CacheConfig {

	@Bean
	Cache<String, ChatClient> chatClientCache() {
		return Caffeine.newBuilder()
				.expireAfterAccess(Duration.ofHours(1))
				.maximumSize(100)
				.build();
	}

	@Bean
	Cache<String, String> accessTokenCache() {
		return Caffeine.newBuilder()
				.expireAfterAccess(Duration.ofHours(1))
				.maximumSize(100)
				.build();
	}


	@Bean
	Caffeine<Object, Object> caffeineConfig() {
		return Caffeine.newBuilder()
				.expireAfterWrite(2, TimeUnit.HOURS)
				.maximumSize(1000);
	}

	@Bean
	CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		cacheManager.setCaffeine(caffeine);
		return cacheManager;
	}

}
