package org.example.medicinalplant.controller;

import lombok.RequiredArgsConstructor;
import org.example.medicinalplant.common.R;
import org.example.medicinalplant.entity.PlantSpecies;
import org.example.medicinalplant.service.PlantSpeciesService;
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
@RequestMapping("/api/species")
@RequiredArgsConstructor
public class SpeciesController {

    private final PlantSpeciesService speciesService;

    @GetMapping
    public R<java.util.List<PlantSpecies>> listAll() {
        return R.ok(speciesService.list());
    }

    @GetMapping("/search")
    public R<java.util.List<PlantSpecies>> search(@RequestParam String keyword) {
        return R.ok(speciesService.search(keyword));
    }

    @PostMapping
    public R<Long> create(@RequestBody PlantSpecies s) {
        speciesService.save(s);
        return R.ok(s.getId());
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody PlantSpecies s) {
        s.setId(id);
        speciesService.updateById(s);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        speciesService.removeById(id);
        return R.ok();
    }
}
