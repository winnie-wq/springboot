package org.example.medicinalplant.controller;

import lombok.RequiredArgsConstructor;
import org.example.medicinalplant.common.R;
import org.example.medicinalplant.entity.KnowledgeResource;
import org.example.medicinalplant.service.KnowledgeResourceService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final KnowledgeResourceService bookService;

    @GetMapping
    public R<java.util.List<KnowledgeResource>> list() {
        return R.ok(bookService.list());
    }

    @GetMapping("/search")
    public R<java.util.List<KnowledgeResource>> search(@RequestParam String keyword) {
        return R.ok(bookService.search(keyword));
    }

    @GetMapping("/{id}")
    public R<KnowledgeResource> get(@PathVariable Long id) {
        return R.ok(bookService.getByIdIncViews(id));
    }

    @PostMapping
    public R<Long> create(@RequestBody KnowledgeResource b) {
        if (b.getCreatedAt() == null) {
            b.setCreatedAt(System.currentTimeMillis());
        }
        if (b.getViews() == null) {
            b.setViews(0);
        }
        bookService.save(b);
        return R.ok(b.getId());
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody KnowledgeResource b) {
        b.setId(id);
        bookService.updateById(b);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        bookService.removeById(id);
        return R.ok();
    }
}
