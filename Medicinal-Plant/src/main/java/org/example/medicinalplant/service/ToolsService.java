package org.example.medicinalplant.service;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

@Service // 加上这个注解 才会被注入到 Spring 容器中
public class ToolsService {

    // @Tool 这个注解 就是用来设置触发 让 ai 调用我们这个业务方法的关键词
    @Tool("根据药用植物的名称查询详细信息。")
    public String searchMessage(@P("药用植物的名称，例如：人参、黄芪、当归") String name) {
        System.out.println("AI捕获到的关键词" + name);
        // TODO 去查询本地知识库的业务代码
        System.out.println(name + "在本地尚无数据，由ai库提供");
        return name + "在本地尚无数据，由ai库提供";
    }
}

