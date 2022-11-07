package ru.lanolin.quoter.backend.controllers.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.IdentificationClass;
import ru.lanolin.quoter.backend.domain.dto.ConverterDtoToEntity;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

public interface RestApiController<
		E extends IdentificationClass<DTO>,
		DTO extends ConverterDtoToEntity<E>,
		ID extends Number> {

	<R extends RestApi<E, ID>> R getService();

	@GetMapping(value = "", params = { "page", "size" }, produces = "application/json")
	default Page<DTO> findAllWithPagination(@SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable page) {
		return getService()
				.findAll(page)
				.map(IdentificationClass::dto);
	}

	@GetMapping(value = "", produces = "application/json")
	default List<DTO> findAll() {
		return getService()
				.findAll()
				.stream()
				.map(IdentificationClass::dto)
				.toList();
	}

	@GetMapping(value = "{id}", produces = "application/json")
	default Optional<DTO> getOne(@PathVariable ID id) {
		return getService()
				.getOne(id)
				.map(IdentificationClass::dto);
	}

	@PutMapping(value = "", consumes = "application/json", produces = "application/json")
	default DTO create(@RequestBody DTO entity) {
		return getService()
				.create(entity.entity())
				.dto();
	}

	@PostMapping(value = "{id}", consumes = "application/json", produces = "application/json")
	default DTO update(@PathVariable ID id, @RequestBody DTO entity) {
		return getService()
				.update(id, entity.entity())
				.dto();
	}

	@DeleteMapping(value = "", consumes = "application/json")
	default void delete(@RequestBody DTO entity) {
		getService().delete(entity.entity());
	}

	@DeleteMapping(value = "{id}")
	default void deleteById(@PathVariable ID id) {
		getService().deleteById(id);
	}

}
