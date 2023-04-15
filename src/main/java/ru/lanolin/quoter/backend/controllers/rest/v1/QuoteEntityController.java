package ru.lanolin.quoter.backend.controllers.rest.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.IdentificationClass;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.dto.QuoteEntityDto;
import ru.lanolin.quoter.backend.exceptions.NotFoundException;
import ru.lanolin.quoter.backend.service.QuoteEntityService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quote")
@CrossOrigin(origins = "*")
public class QuoteEntityController {

    private final QuoteEntityService quoterEntityService;

    @Autowired
    public QuoteEntityController(QuoteEntityService quoterEntityService) {
        this.quoterEntityService = quoterEntityService;
    }

    @GetMapping(value = "", params = {"page", "size"}, produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'GUEST', 'ANON')")
    public Page<QuoteEntityDto> findAllWithPagination(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable page) {
        return quoterEntityService
                .findAll(page)
                .map(IdentificationClass::dto);
    }

    @GetMapping(value = "", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'GUEST', 'ANON')")
    public List<QuoteEntityDto> findAll() {
        return quoterEntityService
                .findAll()
                .stream()
                .map(IdentificationClass::dto)
                .toList();
    }

    @GetMapping(value = "{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'GUEST', 'ANON')")
    public Optional<QuoteEntityDto> getOne(@PathVariable Integer id) {
        return quoterEntityService
                .getOne(id)
                .map(IdentificationClass::dto);
    }

    @PutMapping(value = "", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR')")
    public QuoteEntityDto create(@RequestBody QuoteEntityDto entity) {
        QuoteEntity create = quoterEntityService.create(entity.entity());
        return getOne(create.getId()).orElseThrow(NotFoundException::new);
    }

    @PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR')")
    public QuoteEntityDto update(@PathVariable Integer id, @RequestBody QuoteEntityDto entity) {
        QuoteEntity update = quoterEntityService.update(id, entity.entity());
        return quoterEntityService
                .getOne(update.getId())
                .map(IdentificationClass::dto)
                .orElseThrow(NotFoundException::new);
    }

    @DeleteMapping(value = "", consumes = "application/json")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR')")
    public void delete(@RequestBody QuoteEntityDto entity) {
        quoterEntityService.delete(entity.entity());
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR')")
    public void deleteById(@PathVariable Integer id) {
        quoterEntityService.deleteById(id);
    }
}
