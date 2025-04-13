package com.toelbox.chatbot.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import java.util.Map;

@WritingConverter
class StringMapJsonWriteConverter implements Converter<Map<String, String>, String> {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convert(@NonNull Map<String, String> source) {
		try {
//			System.out.println("🔥 StringMapJsonWriteConverter used! " + source);
			return objectMapper.writeValueAsString(source);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to write JSON map", e);
		}
	}
}
