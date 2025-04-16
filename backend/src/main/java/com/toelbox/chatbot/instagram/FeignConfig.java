package com.toelbox.chatbot.instagram;

import feign.Feign;
import feign.Logger;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import me.bvn13.openfeign.logger.normalized.NormalizedFeignLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder().encoder(new FormEncoder());
    }
    @Bean
    public Logger logger() {
        return new NormalizedFeignLogger();
    }

    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.FULL;
    }

}
