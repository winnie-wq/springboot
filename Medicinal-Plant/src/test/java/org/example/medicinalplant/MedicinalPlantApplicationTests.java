package org.example.medicinalplant;

import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
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

    @Test
    void testDeepSeek() {
        OpenAiChatModel aiChatModel = OpenAiChatModel.builder()
                .modelName("deepseek-chat")
                .apiKey("sk-e3e010f3d9514a73a5805bd1ad629c16")
                .baseUrl("https://api.deepseek.com")
                .build();

        String chat = aiChatModel.chat("你好，请问你是哪位？");
        System.out.println(chat);
    }

    @Test
    void testImageModel() {
        WanxImageModel wanxImageModel = WanxImageModel.builder()
                .modelName("wanx2.1-t2i-plus")//模型的名称有很多
                .apiKey("sk-9d31864caeef4932bfe1e3127a1036de")
                .build();

        Response<Image> generate = wanxImageModel.generate("冬日北京的都市街景，青灰瓦顶、朱红色外墙的两间相邻中式商铺比肩而立，檐下悬挂印有剪纸马的暖光灯笼，在阴天漫射光中投下柔和光晕，映照湿润鹅卵石路面泛起细腻反光。左侧为书法店：靛蓝色老旧的牌匾上以遒劲行书刻着“文字渲染”。店门口的玻璃上挂着一幅字，自上而下，用田英章硬笔写着“专业幻灯片 中英文海报 高级信息图”，落款印章为“1k token”朱砂印。店内的墙上，可以模糊的辨认有三幅竖排的书法作品，第一幅写着着“阿里巴巴”，第二幅写着“通义千问”，第三福写着“图像生成”。一位白发苍苍的老人背对着镜头观赏。右侧为花店，牌匾上以鲜花做成文字“真实质感”；店内多层花架陈列红玫瑰、粉洋牡丹和绿植，门上贴了一个圆形花边标识，标识上写着“2k resolution”，门口摆放了一个彩色霓虹灯，上面写着“细腻刻画 人物 自然 建筑”。两家店中间堆放了一个雪人，举了一老式小黑板，上面用粉笔字写着“Qwen-Image-2.0 正式发布”。街道左侧，年轻情侣依偎在一起，女孩是瘦脸，身穿米白色羊绒大衣，肉色光腿神器。女孩举着心形透明气球，气球印有白色的字：“生图编辑二合一”。里面有一个毛茸茸的卡皮巴拉玩偶。男孩身着剪裁合体的深灰色呢子外套，内搭浅色高领毛衣。街道右侧，一个后背上写着“更小模型，更快速度”的骑手疾驰而过。整条街光影交织、动静相宜。");

        System.out.println(generate);
    }

    @Test
    void testChatHistory() {
        OpenAiChatModel aiChatModel = OpenAiChatModel.builder()
                .modelName("deepseek-chat") //模型的名称
                .apiKey("sk-e3e010f3d9514a73a5805bd1ad629c16")
                .baseUrl("https://api.deepseek.com")
                .build();

        //实现聊天记忆 通常就是需要将之前的聊天记录给带到服务器
        //UserMessage表示的是用户发送的信息import dev.langchain4j.data.message.UserMessage;
        //AiMessage 表示AI响应的数据
        UserMessage userMessage1 = new UserMessage("你好我是李白");
        System.out.println(userMessage1.singleText());
        //聊天内容发送以后 响应的结果
        ChatResponse chatResponse = aiChatModel.chat(userMessage1);
        //响应结果的对象=中就有 ai响应回来的数据
        AiMessage aiMessage1 = chatResponse.aiMessage();
        System.out.println(aiMessage1.text());

        //如果我们想要实现 让ai有上下文记忆 了解上下文的内容 就需要每次都把前面聊天记录带过去
        UserMessage userMessage2 = new UserMessage("我的名字是什么呀?");
        System.out.println(userMessage2.singleText());

        //把前面的聊天内容 也携带到服务器中
        ChatResponse chatResponse1 = aiChatModel.chat(userMessage1, aiMessage1, userMessage2);
        //解析里面的聊天内容
        AiMessage aiMessage2 = chatResponse1.aiMessage();
        System.out.println(aiMessage2.text());

    }

}
