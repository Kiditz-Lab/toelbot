package com.toelbox.chatbot.rag;

import com.toelbox.chatbot.core.AIConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
@RequiredArgsConstructor
class VectorStoreConfig {
	private final AIConfigProperties configProperties;

	OpenAiApi openAiApi() {
		return OpenAiApi.builder()
				.apiKey(configProperties.getOpenAiApiKey())
				.build();
	}

	@Bean
	EmbeddingModel openAIEmbeddingModel() {
		return new OpenAiEmbeddingModel(
				openAiApi(),
				MetadataMode.EMBED,
				OpenAiEmbeddingOptions.builder()
						.model("text-embedding-3-small")
						.build(),
				RetryUtils.DEFAULT_RETRY_TEMPLATE);
	}

//	@Bean
//	EmbeddingModel postgresMlEmbeddingModel(JdbcTemplate jdbcTemplate) {
//		PostgresMlEmbeddingModel embeddingModel = new PostgresMlEmbeddingModel(jdbcTemplate,
//				PostgresMlEmbeddingOptions.builder()
//						.transformer("distilbert-base-uncased")
//						.vectorType(PostgresMlEmbeddingModel.VectorType.PG_VECTOR)
//						.kwargs(Map.of("device", "cpu"))
//						.metadataMode(MetadataMode.EMBED)
//						.build());
//		embeddingModel.afterPropertiesSet();
//		return embeddingModel;
//	}

	@Bean
	VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
		return PgVectorStore.builder(jdbcTemplate, embeddingModel)
				.dimensions(1536)
				.distanceType(COSINE_DISTANCE)
				.indexType(HNSW)
				.initializeSchema(true)
				.schemaName("public")
				.vectorTableName("vector_store")
				.maxDocumentBatchSize(10000)
				.build();
	}

}
