package ru.lanolin.quoter.backend.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestApi<E, ID extends Number> {

	List<E> findAll();
	Optional<E> getOne(ID id);
	E create(E entity);
	E update(ID id, E entity);
	void delete(E entity);
	void deleteById(ID id);

	default Page<E> findAll(Pageable page) {
		return getRepo().findAll(page);
	}

	default<R extends JpaRepository<E,ID>> R getRepo() {
		return null;
	}

}
