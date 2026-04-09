package org.example.medicinalplant.controller;

import dev.langchain4j.service.TokenStream;
import org.example.medicinalplant.config.AiConfig;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    // 注入创建好的聊天代理对象（带聊天记忆）
    @Autowired
    private AiConfig.Assistant assistant;

    // GET /ai/chat?message=你好
    @RequestMapping("/chat")
    public String chat(@RequestParam(defaultValue = "你是谁") String message) {
        return chatLanguageModel.chat(message);
    }

    // GET /ai/chat_memory?message=你好
    @RequestMapping("/chat_memory")
    public String chatMemory(@RequestParam(defaultValue = "你是谁") String message,
                             @RequestParam(defaultValue = "1") int userId) {
        return assistant.chat(userId, message);
    }

    // 创建一个流式接口，带有聊天记忆的
    @RequestMapping(value = "/memory_stream_chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> memoryStreamChat(@RequestParam(defaultValue = "你是谁") String message,
                                         @RequestParam(defaultValue = "1") int userId) {
        TokenStream stream = assistant.stream(userId, message);

        return Flux.create(sink -> stream
                // 设置流式回调：每收到一段就推送到浏览器
                .onPartialResponse(sink::next)
                // 设置完成回调：结束流
                .onCompleteResponse(response -> sink.complete())
                // 设置异常回调：以文本形式推送错误并结束，避免 Spring 尝试用 Map 写入 text/stream
                .onError(error -> {
                    sink.next("❌ " + (error.getMessage() == null ? "服务异常" : error.getMessage()));
                    sink.complete();
                })
                .start());
    }
}

