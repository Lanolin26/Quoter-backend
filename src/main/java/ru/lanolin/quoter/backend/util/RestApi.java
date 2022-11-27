package ru.lanolin.quoter.backend.util;

import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanolin.quoter.backend.domain.IdentificationClass;
import ru.lanolin.quoter.backend.domain.dto.ConverterDtoToEntity;
import ru.lanolin.quoter.backend.exceptions.domain.CheckArgs;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * The interface Rest api.
 *
 * @param <E>
 * 		Entity объект
 * @param <ID>
 * 		Id класс в {@link E} параметре
 */
public interface RestApi<E extends IdentificationClass<ID, ? extends ConverterDtoToEntity<E>>, ID extends Number> {

	default Page<E> findAll(Pageable page) {
		return getRepo().findAll(page).map(e -> e.dto().entity());
	}

	default List<E> findAll() {
		return getRepo().findAll().stream().map(e -> e.dto().entity()).toList();
	}

	default long count() {
		return getRepo().count();
	}

	default Optional<E> getOne(ID id) {
		return getRepo().findById(id).map(e -> e.dto().entity());
	}

	default boolean existById(ID id) {
		return getRepo().existsById(id);
	}

	default boolean exist(E e) {
		return Objects.nonNull(e) && Objects.nonNull(e.getId()) && existById(e.getId());
	}

	/**
	 * <p>Сохраняет элемент в базу данных.</p>
	 * <p>
	 * <b>!При наличии вложенных entity - возвращается объект с заполненным полем {@link IdentificationClass#id}!</b>
	 * </p>
	 *
	 * @param entity
	 *        {@link E} объект сохранения
	 * @return сохраненный объект с выданным {@link IdentificationClass#id}.
	 */
	default E create(@NonNull E entity) throws IncorrectField {
		checkCorrect(entity);
		entity.setId(null);
		return getRepo().save(entity).dto().entity();
	}

	/**
	 * Обновляет элемент по его ID.
	 *
	 * <p>
	 * <b>!При наличии вложенных entity - возвращается объект с заполненным полем {@link IdentificationClass#id}!</b>
	 * </p>
	 *
	 * @param id
	 *        {@link ID}
	 * @param entity
	 *        {@link E}
	 * @return обновленный элемента
	 */
	default E update(ID id, @NonNull E entity) throws IncorrectField {
		checkCorrect(entity);
		Optional<E> inDb = getRepo().findById(id);
		if (inDb.isEmpty()) {
			return create(entity);
		} else {
			E inDbEntity = inDb.get();
			copyProperties(entity, inDbEntity);
			return getRepo().save(inDbEntity).dto().entity();
		}
	}

	default void delete(E entity) {
		getRepo().delete(entity);
	}

	default void deleteById(ID id) {
		getRepo().deleteById(id);
	}

	@SuppressWarnings({ "unchecked" })
	default void checkCorrect(E entity) throws IncorrectField {
		for (CheckArgs<E> eCheckArgs : checks()) {
			if (eCheckArgs.func().test(entity)) {
				throw eCheckArgs.createException((Class<E>) entity.getClass());
			}
		}
	}

	default void copyProperties(E source, E target) {
		BeanUtils.copyProperties(source, target, "id");
	}

	<R extends JpaRepository<E, ID>> R getRepo();

	default List<CheckArgs<E>> checks() {
		return List.of();
	}

}
