package ru.lanolin.quoter.backend.util;

import java.util.List;
import java.util.Optional;

public interface RestApi<E, ID extends Number> {

	List<E> findAll();
	Optional<E> getOne(ID id);
	E create(E entity);
	E update(ID id, E entity);
	void delete(E entity);
	void deleteById(ID id);

}
