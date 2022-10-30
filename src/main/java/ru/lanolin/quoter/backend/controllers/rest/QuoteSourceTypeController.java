package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quote-source-type")
@CrossOrigin(origins = "*")
public class QuoteSourceTypeController {

	private final RestApi<QuoteSourceType, Integer> quoteSourceTypeService;

	@Autowired
	public QuoteSourceTypeController(RestApi<QuoteSourceType, Integer> quoteSourceTypeService) {
		this.quoteSourceTypeService = quoteSourceTypeService;
	}

	@GetMapping(value = "", produces = "application/json")
	public List<QuoteSourceType> findAll() {
		return quoteSourceTypeService.findAll();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	public Optional<QuoteSourceType> getOne(@PathVariable Integer id) {
		return quoteSourceTypeService.getOne(id);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	public QuoteSourceType create(@RequestBody QuoteSourceType entity) {
		return quoteSourceTypeService.create(entity);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	public QuoteSourceType update(@PathVariable Integer id, @RequestBody QuoteSourceType entity) {
		return quoteSourceTypeService.update(id, entity);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	public void delete(@RequestBody QuoteSourceType entity) {
		quoteSourceTypeService.delete(entity);
	}

	@DeleteMapping(value = "{id}")
	public void deleteById(@PathVariable Integer id) {
		quoteSourceTypeService.deleteById(id);
	}

}
