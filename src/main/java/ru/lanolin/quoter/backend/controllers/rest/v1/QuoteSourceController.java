package ru.lanolin.quoter.backend.controllers.rest.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.IdentificationClass;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.dto.QuoteSourceDto;
import ru.lanolin.quoter.backend.exceptions.NotFoundException;
import ru.lanolin.quoter.backend.service.QuoteSourceService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quote-source")
@CrossOrigin(origins = "*")
public class QuoteSourceController {

	private final QuoteSourceService quoteSourceService;

	@Autowired
	public QuoteSourceController(QuoteSourceService quoteSourceService) {
		this.quoteSourceService = quoteSourceService;
	}

	@GetMapping(value = "", params = {"page", "size"}, produces = "application/json")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'GUEST', 'ANON')")
	public Page<QuoteSourceDto> findAllWithPagination(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable page) {
		return quoteSourceService
				.findAll(page)
				.map(IdentificationClass::dto);
	}

	@GetMapping(value = "", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'GUEST', 'ANON')")
	public List<QuoteSourceDto> findAll() {
		return quoteSourceService
				.findAll()
				.stream()
				.map(IdentificationClass::dto)
				.toList();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'GUEST', 'ANON')")
	public Optional<QuoteSourceDto> getOne(@PathVariable Integer id) {
		return quoteSourceService
				.getOne(id)
				.map(IdentificationClass::dto);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR')")
	public QuoteSourceDto create(@RequestBody QuoteSourceDto entity) {
		QuoteSource create = quoteSourceService.create(entity.entity());
		return getOne(create.getId()).orElseThrow(NotFoundException::new);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR')")
	public QuoteSourceDto update(@PathVariable Integer id, @RequestBody QuoteSourceDto entity) {
		QuoteSource update = quoteSourceService.update(id, entity.entity());
		return quoteSourceService
				.getOne(update.getId())
				.map(IdentificationClass::dto)
				.orElseThrow(NotFoundException::new);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR')")
	public void delete(@RequestBody QuoteSourceDto entity) {
		quoteSourceService.delete(entity.entity());
	}

	@DeleteMapping(value = "{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR')")
	public void deleteById(@PathVariable Integer id) {
		quoteSourceService.deleteById(id);
	}
}
