package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
@RequiredArgsConstructor
class RagDeleteServiceActivator {
	@Value("${rag.input.processed}")
	private Resource inputDir;
	private final SimpMessagingTemplate template;

	@ServiceActivator(inputChannel = "ragDeleteChannel")
	void moveFile(RagDelete data) throws IOException {
		File file = data.file();
		log.info("📝 File will be deleted: {}", file.toPath());
		Files.createDirectories(inputDir.getFile().toPath());

		Files.move(file.toPath(), inputDir.getFile().toPath().resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
	}

}
