package ru.lanolin.quoter.backend.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteFromEntity;
import ru.lanolin.quoter.backend.repo.QuoteFromEntityRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@Service
public class QuoteFromEntityService implements RestApi<QuoteFromEntity, Integer> {

	private final QuoteFromEntityRepository repo;

	public QuoteFromEntityService(QuoteFromEntityRepository repo) {
		this.repo = repo;
	}

	@Override
	public List<QuoteFromEntity> findAll() {
		return repo.findAll();
	}

	@Override
	public Optional<QuoteFromEntity> getOne(Integer id) {
		return repo.findById(id);
	}

	@Override
	public QuoteFromEntity create(QuoteFromEntity entity) {
		repo.save(entity);
		return entity;
	}

	@Override
	public QuoteFromEntity update(Integer id, QuoteFromEntity entity) {
		Optional<QuoteFromEntity> inDb = getOne(id);
		if (inDb.isEmpty()) {
			return create(entity);
		} else {
			QuoteFromEntity inDbEntity = inDb.get();
			BeanUtils.copyProperties(entity, inDbEntity, "id");
			return inDbEntity;
		}
	}

	@Override
	public void delete(QuoteFromEntity entity) {
		deleteById(entity.getId());
	}

	@Override
	public void deleteById(Integer id) {
		repo.deleteById(id);
	}
}
