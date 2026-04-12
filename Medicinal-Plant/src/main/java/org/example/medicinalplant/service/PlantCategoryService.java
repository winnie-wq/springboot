package org.example.medicinalplant.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.medicinalplant.entity.PlantCategory;
import org.example.medicinalplant.mapper.PlantCategoryMapper;
import org.example.medicinalplant.vo.CategoryVo;
import org.springframework.stereotype.Service;

@Service
public class PlantCategoryService extends ServiceImpl<PlantCategoryMapper, PlantCategory> {

    public List<CategoryVo> tree() {
        List<PlantCategory> all = list();
        Map<Long, List<PlantCategory>> byParent =
                all.stream()
                        .filter(c -> c.getParentId() != null)
                        .collect(Collectors.groupingBy(PlantCategory::getParentId));

        List<PlantCategory> roots =
                all.stream().filter(c -> c.getParentId() == null).collect(Collectors.toList());

        return roots.stream().map(r -> buildVo(r, byParent)).collect(Collectors.toList());
    }

    private CategoryVo buildVo(PlantCategory c, Map<Long, List<PlantCategory>> byParent) {
        CategoryVo v = new CategoryVo();
        v.setId(c.getId());
        v.setName(c.getName());
        v.setEmoji(c.getEmoji());
        v.setParentId(c.getParentId());
        v.setDescription(c.getDescription());
        List<PlantCategory> subs = byParent.getOrDefault(c.getId(), List.of());
        for (PlantCategory s : subs) {
            v.getChildren().add(buildVo(s, byParent));
        }
        return v;
    }
}
