package org.example.medicinalplant.memory;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.medicinalplant.service.ChatHistoryService;

/** 为每个 memoryId 提供基于数据库的 {@link ChatMemory} 实例 */
public class DatabaseChatMemoryProvider implements ChatMemoryProvider {

    private final ChatHistoryService chatHistoryService;
    private final int maxMessages;
    private final Map<Object, DatabaseChatMemory> memories = new ConcurrentHashMap<>();

    public DatabaseChatMemoryProvider(ChatHistoryService chatHistoryService, int maxMessages) {
        this.chatHistoryService = chatHistoryService;
        this.maxMessages = maxMessages;
    }

    @Override
    public ChatMemory get(Object memoryId) {
        return memories.computeIfAbsent(
                memoryId, id -> new DatabaseChatMemory(id, chatHistoryService, maxMessages));
    }
}
