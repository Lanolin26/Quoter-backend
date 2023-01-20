package ru.lanolin.quoter.backend.controllers.rest.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.IdentificationClass;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.domain.dto.UserEntityDto;
import ru.lanolin.quoter.backend.exceptions.NotFoundException;
import ru.lanolin.quoter.backend.service.UserEntityService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserEntityController {

	private final UserEntityService userEntityService;

	@Autowired
	public UserEntityController(UserEntityService userEntityService) {
		this.userEntityService = userEntityService;
	}

	@GetMapping(value = "", params = { "page", "size" }, produces = "application/json")
	public Page<UserEntityDto> findAllWithPagination(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable page) {
		return userEntityService
				.findAll(page)
				.map(IdentificationClass::dto);
	}

	@GetMapping(value = "", produces = "application/json")
	public List<UserEntityDto> findAll() {
		return userEntityService
				.findAll()
				.stream()
				.map(IdentificationClass::dto)
				.toList();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	public Optional<UserEntityDto> getOne(@PathVariable Integer id) {
		return userEntityService
				.getOne(id)
				.map(IdentificationClass::dto);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	public UserEntityDto create(@RequestBody UserEntityDto entity) {
		UserEntity create = userEntityService.create(entity.entity());
		return getOne(create.getId()).orElseThrow(NotFoundException::new);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	public UserEntityDto update(@PathVariable Integer id, @RequestBody UserEntityDto entity) {
		UserEntity update = userEntityService.update(id, entity.entity());
		return userEntityService
				.getOne(update.getId())
				.map(IdentificationClass::dto)
				.orElseThrow(NotFoundException::new);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	public void delete(@RequestBody UserEntityDto entity) {
		userEntityService.delete(entity.entity());
	}

	@DeleteMapping(value = "{id}")
	public void deleteById(@PathVariable Integer id) {
		userEntityService.deleteById(id);
	}
}
