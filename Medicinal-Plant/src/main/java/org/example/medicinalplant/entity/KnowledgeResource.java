package org.example.medicinalplant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("knowledge_resource")
public class KnowledgeResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String author;

    @TableField("cover_emoji")
    private String coverEmoji;

    private Integer pages;

    private String description;

    private String content;

    private Integer views;

    private Long createdAt;
}
