package com.toelbox.chatbot.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import java.util.Map;

@ReadingConverter
class StringMapJsonReadConverter implements Converter<String, Map<String, String>> {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Map<String, String> convert(@NonNull String source) {
		try {
//			System.out.println("🔥 StringMapJsonReadConverter used! " + source);
			return objectMapper.readValue(source, new TypeReference<>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Failed to read JSON map", e);
		}
	}
}
