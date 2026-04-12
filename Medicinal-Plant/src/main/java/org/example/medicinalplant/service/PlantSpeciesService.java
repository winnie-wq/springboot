package org.example.medicinalplant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.example.medicinalplant.entity.PlantSpecies;
import org.example.medicinalplant.mapper.PlantSpeciesMapper;
import org.springframework.stereotype.Service;

@Service
public class PlantSpeciesService extends ServiceImpl<PlantSpeciesMapper, PlantSpecies> {

    public List<PlantSpecies> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return list();
        }
        String k = keyword.trim();
        return list(
                new LambdaQueryWrapper<PlantSpecies>()
                        .like(PlantSpecies::getName, k)
                        .or()
                        .like(PlantSpecies::getScientificName, k)
                        .or()
                        .like(PlantSpecies::getFeatures, k));
    }
}
