package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quoter")
@CrossOrigin(origins = "*")
public class QuoterEntityController {

	private final RestApi<QuoteEntity, Integer> quoterEntityService;

	@Autowired
	public QuoterEntityController(RestApi<QuoteEntity, Integer> quoterEntityService) {
		this.quoterEntityService = quoterEntityService;
	}

	@GetMapping(value = "/", produces = "application/json")
	public List<QuoteEntity> findAll() {
		return quoterEntityService.findAll();
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public Optional<QuoteEntity> getOne(@PathVariable Integer id) {
		return quoterEntityService.getOne(id);
	}

	@PutMapping(value = "/", consumes = "application/json", produces = "application/json")
	public QuoteEntity create(@RequestBody QuoteEntity entity) {
		return quoterEntityService.create(entity);
	}

	@PostMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
	public QuoteEntity update(@PathVariable Integer id, @RequestBody QuoteEntity entity) {
		return quoterEntityService.update(id, entity);
	}

	@DeleteMapping(value = "/", consumes = "application/json")
	public void delete(@RequestBody QuoteEntity entity) {
		quoterEntityService.delete(entity);
	}

	@DeleteMapping(value = "/{id}")
	public void deleteById(@PathVariable Integer id) {
		quoterEntityService.deleteById(id);
	}

}
