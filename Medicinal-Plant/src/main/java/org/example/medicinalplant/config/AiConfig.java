package org.example.medicinalplant.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.example.medicinalplant.memory.DatabaseChatMemoryProvider;
import org.example.medicinalplant.service.ChatHistoryService;
import org.example.medicinalplant.service.ToolsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    private static final String SYSTEM_PROMPT =
            """
            您是「药用植物知识库」的智能助手，专门为用户解答关于药用植物的问题。

            请遵循以下规则：
            1. 以友好、热情、专业的态度回复用户
            2. 回答要准确、简洁、易懂
            3. 若用户询问的药用植物不在知识库中，请礼貌说明并建议补充资料
            4. 若问题与药用植物无关，请引导用户回到药用植物相关话题
            5. 可结合工具查询药用植物的详细信息（名称、分类、生境、药用部位、特点等）
            6. 回答使用中文
            7. 对用户问候（如「你好」「谢谢」）热情回应并主动提供帮助
            8. 若用户询问时间，请如实告知

            今天的日期是 {{current_date}}。
            """;

    public interface Assistant {

        @SystemMessage(SYSTEM_PROMPT)
        String chat(
                @MemoryId int memoryId,
                @UserMessage String message,
                @V("current_date") String currentDate);

        @SystemMessage(SYSTEM_PROMPT)
        TokenStream stream(
                @MemoryId int memoryId,
                @UserMessage String message,
                @V("current_date") String currentDate);
    }

    @Bean
    public Assistant assistant(
            ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel,
            ToolsService toolsService,
            ChatHistoryService chatHistoryService) {

        ChatMemoryProvider chatMemoryProvider = new DatabaseChatMemoryProvider(chatHistoryService, 20);

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(toolsService)
                .build();
    }
}
