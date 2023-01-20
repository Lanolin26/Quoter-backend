package ru.lanolin.quoter.backend.controllers.rest.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.IdentificationClass;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.domain.dto.QuoteSourceTypeDto;
import ru.lanolin.quoter.backend.exceptions.NotFoundException;
import ru.lanolin.quoter.backend.service.QuoteSourceTypeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quote-source-type")
@CrossOrigin(origins = "*")
public class QuoteSourceTypeController {

	private final QuoteSourceTypeService quoteSourceTypeService;

	@Autowired
	public QuoteSourceTypeController(QuoteSourceTypeService quoteSourceTypeService) {
		this.quoteSourceTypeService = quoteSourceTypeService;
	}

	@GetMapping(value = "", params = { "page", "size" }, produces = "application/json")
	public Page<QuoteSourceTypeDto> findAllWithPagination(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable page) {
		return quoteSourceTypeService
				.findAll(page)
				.map(IdentificationClass::dto);
	}

	@GetMapping(value = "", produces = "application/json")
	public List<QuoteSourceTypeDto> findAll() {
		return quoteSourceTypeService
				.findAll()
				.stream()
				.map(IdentificationClass::dto)
				.toList();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	public Optional<QuoteSourceTypeDto> getOne(@PathVariable Integer id) {
		return quoteSourceTypeService
				.getOne(id)
				.map(IdentificationClass::dto);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	public QuoteSourceTypeDto create(@RequestBody QuoteSourceTypeDto entity) {
		QuoteSourceType create = quoteSourceTypeService.create(entity.entity());
		return getOne(create.getId()).orElseThrow(NotFoundException::new);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	public QuoteSourceTypeDto update(@PathVariable Integer id, @RequestBody QuoteSourceTypeDto entity) {
		QuoteSourceType update = quoteSourceTypeService.update(id, entity.entity());
		return quoteSourceTypeService
				.getOne(update.getId())
				.map(IdentificationClass::dto)
				.orElseThrow(NotFoundException::new);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	public void delete(@RequestBody QuoteSourceTypeDto entity) {
		quoteSourceTypeService.delete(entity.entity());
	}

	@DeleteMapping(value = "{id}")
	public void deleteById(@PathVariable Integer id) {
		quoteSourceTypeService.deleteById(id);
	}
}
