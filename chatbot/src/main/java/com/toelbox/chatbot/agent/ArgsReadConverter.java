package com.toelbox.chatbot.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import java.util.List;

@ReadingConverter
class ArgsReadConverter implements Converter<String, Args> {
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public Args convert(@NonNull String source) {
		try {
			System.out.println("[ArgsReadConverter] Converting JSON string to Args: " + source);
			List<String> values = mapper.readValue(source, new TypeReference<>() {
			});
			return new Args(values);
		} catch (Exception e) {
			throw new RuntimeException("Error deserializing Args", e);
		}
	}
}
