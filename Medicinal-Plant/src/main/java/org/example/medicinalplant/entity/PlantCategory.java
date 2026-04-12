package org.example.medicinalplant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("plant_category")
public class PlantCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String emoji;

    private Long parentId;

    private String description;
}
