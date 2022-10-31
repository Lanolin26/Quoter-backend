package ru.lanolin.quoter.backend.util;

import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;

import java.util.List;
import java.util.Optional;

public interface RestApi<E, ID extends Number> {

	default Page<E> findAll(Pageable page) {
		return getRepo().findAll(page);
	}

	default List<E> findAll() {
		return getRepo().findAll();
	}

	default Optional<E> getOne(ID id) {
		return getRepo().findById(id);
	}

	default E create(@NonNull E entity) {
		checkCorrect(entity);
		return getRepo().save(entity);
	}

	default E update(ID id, @NonNull E entity) {
		Optional<E> inDb = getOne(id);
		if (inDb.isEmpty()) {
			return create(entity);
		} else {
			E inDbEntity = inDb.get();
			copyProperties(entity, inDbEntity);
			checkCorrect(inDbEntity);
			return getRepo().save(inDbEntity);
		}
	}

	default void delete(E entity) {
		getRepo().delete(entity);
	}

	default void deleteById(ID id) {
		getRepo().deleteById(id);
	}

	default void checkCorrect(E entity) throws IncorrectField {
	}

	default void copyProperties(E source, E target) {
		BeanUtils.copyProperties(source, target, "id");
	}

	default <R extends JpaRepository<E, ID>> R getRepo() {
		return null;
	}

}
