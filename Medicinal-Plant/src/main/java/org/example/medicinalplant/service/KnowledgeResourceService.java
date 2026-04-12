package org.example.medicinalplant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.example.medicinalplant.entity.KnowledgeResource;
import org.example.medicinalplant.mapper.KnowledgeResourceMapper;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeResourceService extends ServiceImpl<KnowledgeResourceMapper, KnowledgeResource> {

    public List<KnowledgeResource> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return list();
        }
        String k = keyword.trim();
        return list(
                new LambdaQueryWrapper<KnowledgeResource>()
                        .like(KnowledgeResource::getTitle, k)
                        .or()
                        .like(KnowledgeResource::getAuthor, k)
                        .or()
                        .like(KnowledgeResource::getDescription, k));
    }

    public KnowledgeResource getByIdIncViews(Long id) {
        KnowledgeResource b = getById(id);
        if (b != null) {
            b.setViews((b.getViews() == null ? 0 : b.getViews()) + 1);
            updateById(b);
        }
        return b;
    }
}
