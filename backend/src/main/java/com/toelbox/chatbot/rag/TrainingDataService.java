package com.toelbox.chatbot.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainingDataService {
	@Value("${rag.input.temporary}")
	private Resource targetDir;

	private final TrainingDataRepository repository;
	private final MessageChannel trainingInputChannel;
	private final SimpMessagingTemplate template;
	private final MessageChannel deleteTrainingInputChannel;
	private final VectorStore vectorStore;


	List<TrainingData> uploadFile(UUID agentId, List<MultipartFile> files) throws IOException {
		Files.createDirectories(targetDir.getFile().toPath());
		List<TrainingData> trainingDataList = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String prefix = formatter.format(LocalDateTime.now());
		files.forEach(file -> {
			try {
				String fileName = Objects.requireNonNull(file.getOriginalFilename());
				if (!fileName.matches(".*\\.(pdf|doc|docx|txt)$")) {
					throw new RuntimeException("Invalid file type. Only .pdf, .doc, .docx, and .txt are allowed.");
				}
				Path filePath = targetDir.getFile().toPath().resolve(agentId.toString()).resolve(fileName);
				Files.createDirectories(filePath.getParent());
				TrainingData data = TrainingData.builder().url(fileName).mimeType(file.getContentType()).prefix(prefix).file(prefix + "_" + fileName).type(TrainingType.FILE).size(file.getSize()).agentId(agentId).status(TrainingStatus.PENDING).build();
				data = repository.save(data);
				trainingDataList.add(data);
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				log.error("Exception: ", e);
			}
		});
		return trainingDataList;
	}

	void trainText(UUID agentId, TextContent content) throws IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String prefix = formatter.format(LocalDateTime.now());
		String fileName = agentId.toString() + "_" + "text.txt";
		Path filePath = targetDir.getFile().toPath().resolve(agentId.toString()).resolve(fileName);
		byte[] utf8Bytes = content.content().getBytes(StandardCharsets.UTF_8);
		Files.write(filePath, utf8Bytes);
		TrainingData trainingData = repository.findByAgentIdAndType(agentId, TrainingType.TEXT).map(data -> {
			data.setContent(content.content());
			var jdbc = (JdbcTemplate) vectorStore.getNativeClient().orElse(null);
			String sql = "DELETE FROM vector_store WHERE metadata->>'source' = ? AND metadata->>'agentId' = ?";
			assert jdbc != null;
			jdbc.update(sql, data.getFile(), data.getAgentId().toString());
			data.setPrefix(prefix);
			data.setFile(prefix + "_" + fileName);
			data.setUrl(fileName);
			return data;
		}).orElse(TrainingData.builder().url(fileName).mimeType("text/plain").prefix(prefix).file("").content(content.content()).file(prefix + "_" + fileName).type(TrainingType.TEXT).size(utf8Bytes.length).agentId(agentId).status(TrainingStatus.PENDING).build());
		repository.save(trainingData);
		trainingInputChannel.send(MessageBuilder.withPayload(trainingData).build());
	}


	Page<TrainingData> findAllByAgentIdAndType(UUID agentId, TrainingType type, String search, @Nullable Pageable pageable) {
		return repository.findByAgentIdAndTypeWithSearch(agentId, type, search, pageable);
	}


	void trainFile(UUID agentId) {
		var trainingData = repository.getByAgentIdAndTypeAndStatus(agentId, TrainingType.FILE, TrainingStatus.PENDING);
		trainingInputChannel.send(MessageBuilder.withPayload(trainingData).build());
	}

	void trainWebsite(List<UUID> ids) {
		var trainingData = repository.findAllById(ids);
		trainingInputChannel.send(MessageBuilder.withPayload(trainingData).build());
	}

	void deleteByIds(List<UUID> ids) {
		var list = repository.findAllById(ids);
		list.forEach(trainingData -> {
			deleteTrainingInputChannel.send(MessageBuilder.withPayload(Objects.requireNonNull(trainingData)).build());
		});
		repository.deleteAllById(ids);

	}


	void markSuccess(TrainingData training) {
		log.info("Training success : {}", training.getUrl());
		training.setProgress(false);
		training.setStatus(TrainingStatus.TRAINED);
		repository.save(training);
		template.convertAndSend("/topic/training/%s".formatted(training.getAgentId()), training);
	}

	void markFailed(TrainingData training) {
		log.info("Training failed : {}", training.getUrl());
		training.setProgress(false);
		training.setStatus(TrainingStatus.ERROR);
		repository.save(training);
		template.convertAndSend("/topic/training/%s".formatted(training.getAgentId()), training);
	}

	TrainingSize getSize(UUID agentId) {
		long totalFile = repository.countByAgentId(agentId);
		long fileSize = repository.sumSizeByAgentIdAndType(agentId, TrainingType.FILE);
		long linkSize = repository.sumSizeByAgentIdAndType(agentId, TrainingType.LINK);
		return new TrainingSize(fileSize, linkSize, totalFile, fileSize + linkSize);
	}

	void deleteTrainingData(UUID trainingDataId) {
		var trainingData = repository.findById(trainingDataId).orElse(null);
		deleteTrainingInputChannel.send(MessageBuilder.withPayload(Objects.requireNonNull(trainingData)).build());
	}

}
