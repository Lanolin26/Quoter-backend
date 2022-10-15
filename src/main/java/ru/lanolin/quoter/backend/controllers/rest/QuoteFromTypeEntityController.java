package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.QuoteFromTypeEntity;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quote-from-type")
public class QuoteFromTypeEntityController {

	private final RestApi<QuoteFromTypeEntity, Integer> quoterFromTypeEntityService;

	@Autowired
	public QuoteFromTypeEntityController(RestApi<QuoteFromTypeEntity, Integer> quoterFromTypeEntityService) {
		this.quoterFromTypeEntityService = quoterFromTypeEntityService;
	}

	@GetMapping(value = "", produces = "application/json")
	public List<QuoteFromTypeEntity> findAll() {
		return quoterFromTypeEntityService.findAll();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	public Optional<QuoteFromTypeEntity> getOne(@PathVariable Integer id) {
		return quoterFromTypeEntityService.getOne(id);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	public QuoteFromTypeEntity create(@RequestBody QuoteFromTypeEntity entity) {
		return quoterFromTypeEntityService.create(entity);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	public QuoteFromTypeEntity update(@PathVariable Integer id, @RequestBody QuoteFromTypeEntity entity) {
		return quoterFromTypeEntityService.update(id, entity);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	public void delete(@RequestBody QuoteFromTypeEntity entity) {
		quoterFromTypeEntityService.delete(entity);
	}

	@DeleteMapping(value = "{id}")
	public void deleteById(@PathVariable Integer id) {
		quoterFromTypeEntityService.deleteById(id);
	}

}
