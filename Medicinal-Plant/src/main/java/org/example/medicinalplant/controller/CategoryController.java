package org.example.medicinalplant.controller;

import lombok.RequiredArgsConstructor;
import org.example.medicinalplant.common.R;
import org.example.medicinalplant.entity.PlantCategory;
import org.example.medicinalplant.service.PlantCategoryService;
import org.example.medicinalplant.vo.CategoryVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final PlantCategoryService categoryService;

    @GetMapping("/tree")
    public R<java.util.List<CategoryVo>> tree() {
        return R.ok(categoryService.tree());
    }

    @PostMapping
    public R<Long> create(@RequestBody PlantCategory c) {
        categoryService.save(c);
        return R.ok(c.getId());
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody PlantCategory c) {
        c.setId(id);
        categoryService.updateById(c);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return R.ok();
    }
}
