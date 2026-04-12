package org.example.medicinalplant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("plant_species")
public class PlantSpecies {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String name;

    private String scientificName;

    private String habitat;

    /** 药用部位（前端字段名 diet） */
    private String diet;

    private String features;

    private String conservationStatus;

    private Integer rating;

    private String emoji;
}
