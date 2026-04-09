package org.example.medicinalplant.config;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import org.example.medicinalplant.service.ToolsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // 定义一个 用来实现代理模式的公共接口 代理模式
    public interface Assistant {
        // 代理普通的聊天方法
        String chat(@MemoryId int memoryId, @UserMessage String message);

        // 定义流式响应的方法 响应的数据的类型是 TokenStream 数据流的格式对象
        TokenStream stream(@MemoryId int memoryId, @UserMessage String message);
    }

    // 定义 Spring Bean 来创建助手实例，实现代理对象的创建
    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel,
                              StreamingChatLanguageModel streamingChatLanguageModel,
                              ToolsService toolsService) {
        // 通过 AiServices 对象 来创建代理对象
        return AiServices.builder(Assistant.class)
                // 创建的代理对象的方法中运行时需要的组件在这里设置
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                // 用来实现聊天记忆分离：不同 memoryId 拥有不同的 chatMemory
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .maxMessages(10)
                        .id(memoryId)
                        .build())
                // 实现 function call 还需要注册到这里
                .tools(toolsService)
                .build();
    }
}

