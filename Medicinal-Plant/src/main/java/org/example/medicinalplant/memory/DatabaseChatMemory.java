package org.example.medicinalplant.memory;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.example.medicinalplant.entity.ChatHistory;
import org.example.medicinalplant.service.ChatHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 基于数据库（MySQL / H2 等）的 {@link ChatMemory} 实现 */
public class DatabaseChatMemory implements ChatMemory {

    private static final Logger log = LoggerFactory.getLogger(DatabaseChatMemory.class);

    private final Object memoryId;
    private final ChatHistoryService chatHistoryService;
    private final int maxMessages;

    public DatabaseChatMemory(Object memoryId, ChatHistoryService chatHistoryService, int maxMessages) {
        this.memoryId = memoryId;
        this.chatHistoryService = chatHistoryService;
        this.maxMessages = maxMessages;
    }

    @Override
    public Object id() {
        return memoryId;
    }

    @Override
    public void add(ChatMessage message) {
        try {
            int userId = resolveUserId(memoryId);
            String role = getRoleString(message);
            String content = message.text();

            chatHistoryService.saveMessage(userId, role, content, null);
        } catch (Exception e) {
            log.warn("保存聊天消息失败: {}", e.getMessage());
        }
    }

    @Override
    public List<ChatMessage> messages() {
        try {
            int userId = resolveUserId(memoryId);
            List<ChatHistory> histories = chatHistoryService.getRecentMessages(userId, maxMessages);

            return histories.stream()
                    .filter(history -> history != null && history.getContent() != null)
                    .map(this::convertToChatMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.warn("读取聊天消息失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void clear() {
        try {
            int userId = resolveUserId(memoryId);
            chatHistoryService.clearUserMessages(userId);
        } catch (Exception e) {
            log.warn("清空聊天消息失败: {}", e.getMessage());
        }
    }

    /**
     * LangChain4j 传入的 memoryId 可能为 Integer、Long、Number 或（未匹配到 @MemoryId 时）字符串 {@code "default"}，
     * 强转为 Integer 会失败并导致读写历史均为空，最终触发 {@code messages cannot be null or empty}。
     */
    private static int resolveUserId(Object memoryId) {
        if (memoryId == null) {
            throw new IllegalArgumentException("memoryId (userId) 不能为空");
        }
        if (memoryId instanceof Integer i) {
            return i;
        }
        if (memoryId instanceof Long l) {
            return Math.toIntExact(l);
        }
        if (memoryId instanceof Number n) {
            return n.intValue();
        }
        if (memoryId instanceof String s) {
            String t = s.trim();
            if (t.isEmpty() || "default".equalsIgnoreCase(t)) {
                return 1;
            }
            return Integer.parseInt(t);
        }
        throw new IllegalArgumentException("不支持的 memoryId 类型: " + memoryId.getClass().getName());
    }

    private String getRoleString(ChatMessage message) {
        if (message instanceof UserMessage) {
            return "user";
        }
        if (message instanceof AiMessage) {
            return "assistant";
        }
        if (message instanceof SystemMessage) {
            return "system";
        }
        return "unknown";
    }

    private ChatMessage convertToChatMessage(ChatHistory history) {
        if (history.getContent() == null) {
            return null;
        }

        return switch (history.getRole()) {
            case "user" -> UserMessage.from(history.getContent());
            case "assistant" -> AiMessage.from(history.getContent());
            case "system" -> SystemMessage.from(history.getContent());
            default -> UserMessage.from(history.getContent());
        };
    }
}
