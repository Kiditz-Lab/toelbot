package com.toelbox.chatbot.instagram;

import feign.Logger;
import feign.slf4j.Slf4jLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Logger logger() {
        return new Slf4jLogger();
    }

    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.FULL;
    }

}
