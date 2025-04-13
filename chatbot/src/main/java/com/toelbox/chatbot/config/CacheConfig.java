package com.toelbox.chatbot.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, ChatClient> chatClientCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofHours(2))
                .maximumSize(500)
                .build();
    }
}
