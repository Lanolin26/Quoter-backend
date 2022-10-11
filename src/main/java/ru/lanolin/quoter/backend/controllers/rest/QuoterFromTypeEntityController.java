package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.QuoterFromTypeEntity;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quoter-from-type")
public class QuoterFromTypeEntityController {

	private final RestApi<QuoterFromTypeEntity, Integer> quoterFromTypeEntityService;

	@Autowired
	public QuoterFromTypeEntityController(RestApi<QuoterFromTypeEntity, Integer> quoterFromTypeEntityService) {
		this.quoterFromTypeEntityService = quoterFromTypeEntityService;
	}

	@GetMapping(value = "/", produces = "application/json")
	public List<QuoterFromTypeEntity> findAll() {
		return quoterFromTypeEntityService.findAll();
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public Optional<QuoterFromTypeEntity> getOne(@PathVariable Integer id) {
		return quoterFromTypeEntityService.getOne(id);
	}

	@PutMapping(value = "/", consumes = "application/json", produces = "application/json")
	public QuoterFromTypeEntity create(@RequestBody QuoterFromTypeEntity entity) {
		return quoterFromTypeEntityService.create(entity);
	}

	@PostMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
	public QuoterFromTypeEntity update(@PathVariable Integer id, @RequestBody QuoterFromTypeEntity entity) {
		return quoterFromTypeEntityService.update(id, entity);
	}

	@DeleteMapping(value = "/", consumes = "application/json")
	public void delete(@RequestBody QuoterFromTypeEntity entity) {
		quoterFromTypeEntityService.delete(entity);
	}

	@DeleteMapping(value = "/{id}")
	public void deleteById(@PathVariable Integer id) {
		quoterFromTypeEntityService.deleteById(id);
	}

}
