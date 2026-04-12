package org.example.medicinalplant.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CategoryVo {
    private Long id;
    private String name;
    private String emoji;
    private Long parentId;
    private String description;
    private List<CategoryVo> children = new ArrayList<>();
}
