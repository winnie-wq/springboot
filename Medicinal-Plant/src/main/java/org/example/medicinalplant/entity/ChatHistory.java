package org.example.medicinalplant.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("medicinal_chat_history")
public class ChatHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer userId;

    private String sessionId;

    private String role;

    private String content;

    private Integer tokensUsed;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
