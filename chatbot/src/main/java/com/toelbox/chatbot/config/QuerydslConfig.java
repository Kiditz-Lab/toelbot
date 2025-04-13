package com.toelbox.chatbot.config;

import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.spring.SpringConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class QuerydslConfig {

    @Bean
    public SQLQueryFactory sqlQueryFactory(DataSource dataSource) {
        com.querydsl.sql.Configuration querydslConfig = 
            new com.querydsl.sql.Configuration(new PostgreSQLTemplates());

        return new SQLQueryFactory(querydslConfig, new SpringConnectionProvider(dataSource));
    }
}
