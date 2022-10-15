package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserEntityController {

	private final RestApi<UserEntity, Integer> userEntityService;

	@Autowired
	public UserEntityController(RestApi<UserEntity, Integer> userEntityService) {
		this.userEntityService = userEntityService;
	}

	@GetMapping(value = "", produces = "application/json")
	public List<UserEntity> findAll() {
		return userEntityService.findAll();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	public Optional<UserEntity> getOne(@PathVariable Integer id) {
		return userEntityService.getOne(id);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	public UserEntity create(@RequestBody UserEntity entity) {
		return userEntityService.create(entity);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	public UserEntity update(@PathVariable Integer id, @RequestBody UserEntity entity) {
		return userEntityService.update(id, entity);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	public void delete(@RequestBody UserEntity entity) {
		userEntityService.delete(entity);
	}

	@DeleteMapping(value = "{id}")
	public void deleteById(@PathVariable Integer id) {
		userEntityService.deleteById(id);
	}
}
