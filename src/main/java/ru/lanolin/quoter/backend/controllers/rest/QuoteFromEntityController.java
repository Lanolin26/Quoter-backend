package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.QuoteFromEntity;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quote-from")
@CrossOrigin(origins = "*")
public class QuoteFromEntityController {

	private final RestApi<QuoteFromEntity, Integer> quoterFromEntityService;

	@Autowired
	public QuoteFromEntityController(RestApi<QuoteFromEntity, Integer> quoterFromEntityService) {
		this.quoterFromEntityService = quoterFromEntityService;
	}

	@GetMapping(value = "", produces = "application/json")
	public List<QuoteFromEntity> findAll() {
		return quoterFromEntityService.findAll();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	public Optional<QuoteFromEntity> getOne(@PathVariable Integer id) {
		return quoterFromEntityService.getOne(id);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	public QuoteFromEntity create(@RequestBody QuoteFromEntity entity) {
		return quoterFromEntityService.create(entity);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	public QuoteFromEntity update(@PathVariable Integer id, @RequestBody QuoteFromEntity entity) {
		return quoterFromEntityService.update(id, entity);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	public void delete(@RequestBody QuoteFromEntity entity) {
		quoterFromEntityService.delete(entity);
	}

	@DeleteMapping(value = "{id}")
	public void deleteById(@PathVariable Integer id) {
		quoterFromEntityService.deleteById(id);
	}

}
