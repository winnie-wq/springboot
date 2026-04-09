package org.example.medicinalplant;

import dev.langchain4j.community.model.dashscope.WanxImageModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

    // 文本的向量化的demo
    @Test
    void testEmbed() {
        // 创建向量化模型 -- 用来讲文本数据 向量化的模型 采用阿里的通义向量模型
        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                // 用的是 阿里的模型广场里面的模型 这里使用阿里的密钥
                .apiKey("sk-ef57830297d94df5b408dc85f987ed81")
                .build();

        // 向量化文本
        // 文本向量化 将我是小红 转化成 数字数据：Embedding { vector = [...] }
        // 转成数字以后 才方便用各种算法（余弦相似度或欧几里得距离）来计算，数据之间的相似度，数据之间相似度越高
        // 越高 数据越相近 数据原本的字符串语义就越相近
        Response<Embedding> embed = embeddingModel.embed("我是小红");
        // 输出向量化的结果
        System.out.println(embed.content().toString());
        // 输出向量化数据的长度
        System.out.println(embed.content().vector().length);
    }

    @Test
    void testEmbedContent() {
        // 1、构建向量化模型
        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey("sk-9d31864caeef4932bfe1e3127a1036de")
                .build();

        // 2、创建向量化数据库
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 3、将知识数据向量化存储到向量数据库
        TextSegment textSegment1 = TextSegment.from("""
                人参：五加科植物，性温，味甘微苦。常用于补气固脱、健脾益肺、生津养血。
                适用于气虚乏力、食欲不振、久病体虚等症状。
                """);
        Response<Embedding> embedded = embeddingModel.embed(textSegment1);
        Embedding embedding1 = embedded.content();
        embeddingStore.add(embedding1, textSegment1);

        TextSegment textSegment2 = TextSegment.from("""
                黄芪：豆科植物，性微温，味甘。常用于补气升阳、固表止汗、利水消肿、托毒生肌。
                常见于气虚自汗、体倦乏力、脾肺气虚等证。
                """);
        embeddingStore.add(embeddingModel.embed(textSegment2).content(), textSegment2);

        // 4、构建检索请求并设置参数
        Response<Embedding> embed = embeddingModel.embed("黄芪有什么特点呀？");
        Embedding queryEmbedding = embed.content();
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1) // 返回多少个结果
                .minScore(0.7) // 设置相似度阈值
                .build();

        // 5、进行检索
        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(searchRequest);

        System.out.println("黄芪有什么特点呀？");
        if (!result.matches().isEmpty()) {
            System.out.println(result.matches().get(0).score()); // 输出分数
            System.out.println(result.matches().get(0).embedded().text()); // 输出匹配内容
        } else {
            System.out.println("没有匹配的结果，向量数据库中没有对应的数据");
        }
    }

    @Test
    void testDocument() {
        String url = "rag/terms-of-service.txt";
        //1、用文档加载器 加载文档
        Document document = ClassPathDocumentLoader.loadDocument(
                url
                , new TextDocumentParser());

        //输出读取到的文档
//        System.out.println(document);

        //分词器分词
        DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(
                //200个字符为一段
                50,
                //设置重叠的字符数量
                25);

        //将文章进行分词操作 AI Agent应用开发（ai应用 给大模型设置角色、设置调用的函数、接入知识库 例子智能客服）
        List<TextSegment> textSegments = splitter.split(document);

        //文本向量化
        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey("sk-ef57830297d94df5b408dc85f987ed81").build();
        Response<List<Embedding>> listResponse = embeddingModel.embedAll(textSegments);
        //得到他的向量化数据
        List<Embedding> embeddings = listResponse.content();

        //向量化存储 TextSegment向量化数据对应的文本
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        //将向量化数据 和 对应的原始文本 存放到 向量数据库中
        embeddingStore.addAll(embeddings, textSegments);

        //向量化检索
        Response<Embedding> embed = embeddingModel.embed("海马是什么啊");
        //因为 EmbeddingSearchRequest要的是向量化的数据
        Embedding content = embed.content();
        //构建检索的对象
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(content)
                .minScore(0.7)
                .maxResults(3).build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(searchRequest);

        //输出向量化的结果
        if (result.matches().isEmpty()) {
            System.out.println("没有匹配的结果");
        } else {
            for (EmbeddingMatch<TextSegment> match : result.matches()) {
                System.out.println("==================================================");
                System.out.println("匹配的分数是：" + match.score());
                System.out.println("匹配的向量化数据:" + match.embedding().toString());
                System.out.println("匹配的文本" + match.embedded().text());
                System.out.println("--------------------------------------------------");
            }
        }
    }

}
