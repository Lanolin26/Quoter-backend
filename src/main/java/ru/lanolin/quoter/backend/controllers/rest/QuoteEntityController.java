package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
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

	@GetMapping(value = "", params = { "page", "size" }, produces = "application/json")
	public Page<QuoteEntity> findAllWithPagination(
			@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable page) {
		return quoterEntityService.findAll(page);
	}

	@GetMapping(value = "", produces = "application/json")
	public List<QuoteEntity> findAll() {
		return quoterEntityService.findAll();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	public Optional<QuoteEntity> getOne(@PathVariable Integer id) {
		return quoterEntityService.getOne(id);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	public QuoteEntity create(@RequestBody QuoteEntity entity) {
		return quoterEntityService.create(entity);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	public QuoteEntity update(@PathVariable Integer id, @RequestBody QuoteEntity entity) {
		return quoterEntityService.update(id, entity);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	public void delete(@RequestBody QuoteEntity entity) {
		quoterEntityService.delete(entity);
	}

	@DeleteMapping(value = "{id}")
	public void deleteById(@PathVariable Integer id) {
		quoterEntityService.deleteById(id);
	}

}
