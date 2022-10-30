package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quote-source")
@CrossOrigin(origins = "*")
public class QuoteSourceController {

	private final RestApi<QuoteSource, Integer> quoteSourceService;

	@Autowired
	public QuoteSourceController(RestApi<QuoteSource, Integer> quoteSourceService) {
		this.quoteSourceService = quoteSourceService;
	}

	@GetMapping(value = "", produces = "application/json")
	public List<QuoteSource> findAll() {
		return quoteSourceService.findAll();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	public Optional<QuoteSource> getOne(@PathVariable Integer id) {
		return quoteSourceService.getOne(id);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	public QuoteSource create(@RequestBody QuoteSource entity) {
		return quoteSourceService.create(entity);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	public QuoteSource update(@PathVariable Integer id, @RequestBody QuoteSource entity) {
		return quoteSourceService.update(id, entity);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	public void delete(@RequestBody QuoteSource entity) {
		quoteSourceService.delete(entity);
	}

	@DeleteMapping(value = "{id}")
	public void deleteById(@PathVariable Integer id) {
		quoteSourceService.deleteById(id);
	}

}
