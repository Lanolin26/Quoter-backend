package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

public interface RestApiController<E, ID extends Number> {

	<R extends RestApi<E, ID>> R getService();

	@GetMapping(value = "", params = { "page", "size" }, produces = "application/json")
	default Page<E> findAllWithPagination(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable page) {
		return getService().findAll(page);
	}

	@GetMapping(value = "", produces = "application/json")
	default List<E> findAll() {
		return getService().findAll();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	default Optional<E> getOne(@PathVariable ID id) {
		return getService().getOne(id);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	default E create(@RequestBody E entity) {
		return getService().create(entity);
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	default E update(@PathVariable ID id, @RequestBody E entity) {
		return getService().update(id, entity);
	}

	@DeleteMapping(value = "", consumes = "application/json")
	default void delete(@RequestBody E entity) {
		getService().delete(entity);
	}

	@DeleteMapping(value = "{id}")
	default void deleteById(@PathVariable ID id) {
		getService().deleteById(id);
	}

}
