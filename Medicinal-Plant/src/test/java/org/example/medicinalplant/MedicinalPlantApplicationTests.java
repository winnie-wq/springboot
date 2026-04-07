package org.example.medicinalplant;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MedicinalPlantApplicationTests {

    @Test
    void contextLoads() {
        OpenAiChatModel aiChatModel = OpenAiChatModel.builder()
                .modelName("gpt-4o-mini")
                .apiKey("demo")
                .build();

        String chat = aiChatModel.chat("你好，你是谁呀");
        System.out.println(chat);
    }

}
