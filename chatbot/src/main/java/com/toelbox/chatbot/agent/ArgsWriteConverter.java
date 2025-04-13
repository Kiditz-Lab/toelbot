package com.toelbox.chatbot.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

@WritingConverter
class ArgsWriteConverter implements Converter<Args, String> {
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public String convert(@NonNull Args source) {
		try {
			System.out.println("[ArgsWriteConverter] Converting Args to JSON string: " + source.values());
			return mapper.writeValueAsString(source.values());
		} catch (Exception e) {
			throw new RuntimeException("Error serializing Args", e);
		}
	}
}
