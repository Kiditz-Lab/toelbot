package com.toelbox.chatbot.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.lang.NonNull;

import java.util.List;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {
	@Override
	protected @NonNull List<?> userConverters() {
		return List.of(
				new StringMapJsonReadConverter(),
				new StringMapJsonWriteConverter()
		);
	}
}
