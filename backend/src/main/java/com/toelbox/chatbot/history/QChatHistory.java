package com.toelbox.chatbot.history;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.RelationalPathBase;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

public class QChatHistory extends RelationalPathBase<ChatHistory> {

    private static final long serialVersionUID = 1L;

    public static final QChatHistory chatHistory = new QChatHistory("chatHistory");

    public final SimplePath<UUID> id = createSimple("id", UUID.class);
    public final SimplePath<UUID> agentId = createSimple("agentId", UUID.class);
    public final StringPath chatId = createString("chatId");
    public final StringPath botMessage = createString("botMessage");
    public final StringPath userMessage = createString("userMessage");
    public final StringPath ipAddress = createString("ipAddress");
    public final StringPath countryCode = createString("countryCode");
    public final StringPath country = createString("country");
    public final StringPath regionName = createString("regionName");
    public final StringPath regionCode = createString("regionCode");
    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);
    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);
    public final StringPath model = createString("model");
    public final DateTimePath<LocalDateTime> createdAt = createDateTime("createdAt", LocalDateTime.class);

    public QChatHistory(String variable) {
        super(ChatHistory.class, forVariable(variable), null, "chat_history");
        addMetadata();
    }

    public QChatHistory(Path<? extends ChatHistory> path) {
        super(path.getType(), path.getMetadata(), null, "chat_history");
        addMetadata();
    }

    public QChatHistory(PathMetadata metadata) {
        super(ChatHistory.class, metadata, null, "chat_history");
        addMetadata();
    }
    private void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").ofType(java.sql.Types.OTHER));
        addMetadata(agentId, ColumnMetadata.named("agent_id").ofType(java.sql.Types.OTHER));
        addMetadata(chatId, ColumnMetadata.named("chat_id").ofType(java.sql.Types.VARCHAR));
        addMetadata(botMessage, ColumnMetadata.named("bot_message").ofType(java.sql.Types.VARCHAR));
        addMetadata(userMessage, ColumnMetadata.named("user_message").ofType(java.sql.Types.VARCHAR));
        addMetadata(ipAddress, ColumnMetadata.named("ip_address").ofType(java.sql.Types.VARCHAR));
        addMetadata(countryCode, ColumnMetadata.named("country_code").ofType(java.sql.Types.VARCHAR));
        addMetadata(country, ColumnMetadata.named("country").ofType(java.sql.Types.VARCHAR));
        addMetadata(regionName, ColumnMetadata.named("region_name").ofType(java.sql.Types.VARCHAR));
        addMetadata(regionCode, ColumnMetadata.named("region_code").ofType(java.sql.Types.VARCHAR));
        addMetadata(latitude, ColumnMetadata.named("latitude").ofType(java.sql.Types.DOUBLE));
        addMetadata(longitude, ColumnMetadata.named("longitude").ofType(java.sql.Types.DOUBLE));
        addMetadata(model, ColumnMetadata.named("model").ofType(java.sql.Types.VARCHAR));
        addMetadata(createdAt, ColumnMetadata.named("created_at").ofType(java.sql.Types.TIMESTAMP));
    }

}
