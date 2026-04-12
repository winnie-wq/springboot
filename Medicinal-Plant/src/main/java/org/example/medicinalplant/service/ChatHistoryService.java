package org.example.medicinalplant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.medicinalplant.entity.ChatHistory;
import org.example.medicinalplant.mapper.ChatHistoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatHistoryService extends ServiceImpl<ChatHistoryMapper, ChatHistory> {

    private static final int MAX_MESSAGES_PER_USER = 50;

    /**
     * 保存用户消息
     */
    @Transactional
    public void saveMessage(Integer userId, String role, String content, Integer tokensUsed) {
        String sessionId = getOrCreateSessionId(userId);

        ChatHistory history = new ChatHistory();
        history.setUserId(userId);
        history.setSessionId(sessionId);
        history.setRole(role);
        history.setContent(content);
        history.setTokensUsed(tokensUsed != null ? tokensUsed : 0);

        save(history);

        cleanOldMessages(userId);
    }

    /**
     * 保存消息（指定 sessionId）
     */
    @Transactional
    public void saveMessageWithSession(
            Integer userId, String sessionId, String role, String content, Integer tokensUsed) {
        ChatHistory history = new ChatHistory();
        history.setUserId(userId);
        history.setSessionId(sessionId);
        history.setRole(role);
        history.setContent(content);
        history.setTokensUsed(tokensUsed != null ? tokensUsed : 0);

        save(history);

        cleanOldMessages(userId);
    }

    /**
     * 获取用户的最近对话记录（用于上下文）
     */
    public List<ChatHistory> getRecentMessages(Integer userId, int limit) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getUserId, userId)
                .orderByDesc(ChatHistory::getCreatedAt)
                .last("LIMIT " + limit);

        List<ChatHistory> list = list(wrapper);
        list.sort(
                Comparator.comparing(ChatHistory::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())));
        return list;
    }

    /**
     * 获取会话的所有消息
     */
    public List<ChatHistory> getSessionMessages(String sessionId) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getSessionId, sessionId).orderByAsc(ChatHistory::getCreatedAt);
        return list(wrapper);
    }

    /**
     * 获取或创建会话 ID
     */
    public String getOrCreateSessionId(Integer userId) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getUserId, userId)
                .orderByDesc(ChatHistory::getCreatedAt)
                .last("LIMIT 1");

        ChatHistory lastChat = getOne(wrapper, false);
        if (lastChat != null && lastChat.getSessionId() != null) {
            return lastChat.getSessionId();
        }

        return UUID.randomUUID().toString();
    }

    /**
     * 清理旧消息，每个用户最多保留 {@value #MAX_MESSAGES_PER_USER} 条
     */
    private void cleanOldMessages(Integer userId) {
        long count = count(new LambdaQueryWrapper<ChatHistory>().eq(ChatHistory::getUserId, userId));
        if (count <= MAX_MESSAGES_PER_USER) {
            return;
        }

        int toRemove = (int) (count - MAX_MESSAGES_PER_USER);
        LambdaQueryWrapper<ChatHistory> w = new LambdaQueryWrapper<>();
        w.eq(ChatHistory::getUserId, userId).orderByAsc(ChatHistory::getCreatedAt).last("LIMIT " + toRemove);

        List<ChatHistory> oldest = list(w);
        if (oldest.isEmpty()) {
            return;
        }
        List<Long> ids = oldest.stream().map(ChatHistory::getId).collect(Collectors.toList());
        removeBatchByIds(ids);
    }

    /**
     * 清空用户的所有对话记录
     */
    @Transactional
    public void clearUserMessages(Integer userId) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getUserId, userId);
        remove(wrapper);
    }

    /**
     * 清空会话的所有记录
     */
    @Transactional
    public void clearSessionMessages(String sessionId) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getSessionId, sessionId);
        remove(wrapper);
    }

    /**
     * 获取用户对话统计
     */
    public UserChatStats getUserStats(Integer userId) {
        long totalMessages =
                count(new LambdaQueryWrapper<ChatHistory>().eq(ChatHistory::getUserId, userId));
        long userMessages =
                count(
                        new LambdaQueryWrapper<ChatHistory>()
                                .eq(ChatHistory::getUserId, userId)
                                .eq(ChatHistory::getRole, "user"));
        long assistantMessages =
                count(
                        new LambdaQueryWrapper<ChatHistory>()
                                .eq(ChatHistory::getUserId, userId)
                                .eq(ChatHistory::getRole, "assistant"));

        LambdaQueryWrapper<ChatHistory> lastWrapper = new LambdaQueryWrapper<>();
        lastWrapper.eq(ChatHistory::getUserId, userId)
                .orderByDesc(ChatHistory::getCreatedAt)
                .last("LIMIT 1");
        ChatHistory lastChat = getOne(lastWrapper, false);

        UserChatStats stats = new UserChatStats();
        stats.setTotalMessages((int) totalMessages);
        stats.setUserMessages((int) userMessages);
        stats.setAssistantMessages((int) assistantMessages);
        stats.setLastChatTime(lastChat != null ? lastChat.getCreatedAt() : null);

        return stats;
    }

    /** 用户对话统计 */
    public static class UserChatStats {
        private int totalMessages;
        private int userMessages;
        private int assistantMessages;
        private LocalDateTime lastChatTime;

        public int getTotalMessages() {
            return totalMessages;
        }

        public void setTotalMessages(int totalMessages) {
            this.totalMessages = totalMessages;
        }

        public int getUserMessages() {
            return userMessages;
        }

        public void setUserMessages(int userMessages) {
            this.userMessages = userMessages;
        }

        public int getAssistantMessages() {
            return assistantMessages;
        }

        public void setAssistantMessages(int assistantMessages) {
            this.assistantMessages = assistantMessages;
        }

        public LocalDateTime getLastChatTime() {
            return lastChatTime;
        }

        public void setLastChatTime(LocalDateTime lastChatTime) {
            this.lastChatTime = lastChatTime;
        }
    }
}
