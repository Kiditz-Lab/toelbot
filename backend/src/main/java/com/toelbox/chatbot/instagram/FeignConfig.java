package com.toelbox.chatbot.instagram;

import feign.Logger;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import me.bvn13.openfeign.logger.normalized.NormalizedFeignLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Logger logger() {
        return new NormalizedFeignLogger();
    }

    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.FULL;
    }

}
