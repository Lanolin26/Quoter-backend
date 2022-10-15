package ru.lanolin.quoter.backend.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteFromTypeEntity;
import ru.lanolin.quoter.backend.repo.QuoteFromTypeEntityRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@Service
public class QuoteFromTypeEntityService implements RestApi<QuoteFromTypeEntity, Integer> {

	private final QuoteFromTypeEntityRepository repo;

	@Autowired
	public QuoteFromTypeEntityService(QuoteFromTypeEntityRepository repo) {
		this.repo = repo;
	}


	@Override
	public List<QuoteFromTypeEntity> findAll() {
		return repo.findAll();
	}

	@Override
	public Optional<QuoteFromTypeEntity> getOne(Integer id) {
		return repo.findById(id);
	}

	@Override
	public QuoteFromTypeEntity create(QuoteFromTypeEntity entity) {
		repo.save(entity);
		return entity;
	}

	@Override
	public QuoteFromTypeEntity update(Integer id, QuoteFromTypeEntity entity) {
		Optional<QuoteFromTypeEntity> inDb = getOne(id);
		if (inDb.isEmpty()) {
			return create(entity);
		} else {
			QuoteFromTypeEntity inDbEntity = inDb.get();
			BeanUtils.copyProperties(entity, inDbEntity, "id");
			return inDbEntity;
		}
	}

	@Override
	public void delete(QuoteFromTypeEntity entity) {
		deleteById(entity.getId());
	}

	@Override
	public void deleteById(Integer id) {
		repo.deleteById(id);
	}
}
