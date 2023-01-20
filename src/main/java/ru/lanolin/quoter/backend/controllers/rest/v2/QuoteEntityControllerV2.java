package ru.lanolin.quoter.backend.controllers.rest.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityIdsInfo;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityInfo;
import ru.lanolin.quoter.backend.service.QuoteEntityService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quote/v2")
@CrossOrigin(origins = "*")
public class QuoteEntityControllerV2 {

    private final QuoteEntityService quoterEntityService;

    @Autowired
    public QuoteEntityControllerV2(QuoteEntityService quoterEntityService) {
        this.quoterEntityService = quoterEntityService;
    }

    @GetMapping(value = "with_id/", params = {"page", "size"}, produces = "application/json")
    public Page<QuoteEntityIdsInfo> findAllIdsInfoPagination(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable page) {
        return quoterEntityService.findIdsInfoAll(page);
    }

    @GetMapping(value = "with_id/", produces = "application/json")
    public List<QuoteEntityIdsInfo> findAllIdsInfo() {
        return quoterEntityService.findIdsInfoAll();
    }

    @GetMapping(value = "with_id/{id}", produces = "application/json")
    public Optional<QuoteEntityIdsInfo> getOneIdsInfo(@PathVariable Integer id) {
        return quoterEntityService.getOneIdsInfo(id);
    }

    @GetMapping(value = "", params = {"page", "size"}, produces = "application/json")
    public Page<QuoteEntityInfo> findAllInfoPagination(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable page) {
        return quoterEntityService.findInfoAll(page);
    }

    @GetMapping(value = "", produces = "application/json")
    public List<QuoteEntityInfo> findAllInfo() {
        return quoterEntityService.findInfoAll();
    }

    @GetMapping(value = "{id}", produces = "application/json")
    public Optional<QuoteEntityInfo> getOneInfo(@PathVariable Integer id) {
        return quoterEntityService.getOneInfo(id);
    }

    @PutMapping(value = "", consumes = "application/json", produces = "application/json")
    public QuoteEntityIdsInfo create(@RequestBody QuoteEntityIdsInfo entity) {
        QuoteEntityIdsInfo create = quoterEntityService.create(entity);
        return create;
    }

    @PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
    public QuoteEntityIdsInfo update(@PathVariable Integer id, @RequestBody QuoteEntityIdsInfo entity) {
        QuoteEntityIdsInfo update = quoterEntityService.update(id, entity);
        return update;
    }

    @DeleteMapping(value = "", consumes = "application/json")
    public void delete(@RequestBody QuoteEntityIdsInfo entity) {
        quoterEntityService.delete(entity);
    }

    @DeleteMapping(value = "{id}")
    public void deleteById(@PathVariable Integer id) {
        quoterEntityService.deleteById(id);
    }
}
