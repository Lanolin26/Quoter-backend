package ru.lanolin.quoter.backend.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.repo.QuoteSourceTypeRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@Service
public class QuoteSourceTypeService implements RestApi<QuoteSourceType, Integer> {

	private final QuoteSourceTypeRepository repo;

	@Autowired
	public QuoteSourceTypeService(QuoteSourceTypeRepository repo) {
		this.repo = repo;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public QuoteSourceTypeRepository getRepo() {
		return repo;
	}

	@Override
	public List<QuoteSourceType> findAll() {
		return repo.findAll();
	}

	@Override
	public Optional<QuoteSourceType> getOne(Integer id) {
		return repo.findById(id);
	}

	@Override
	public QuoteSourceType create(QuoteSourceType entity) {
		repo.save(entity);
		return entity;
	}

	@Override
	public QuoteSourceType update(Integer id, QuoteSourceType entity) {
		Optional<QuoteSourceType> inDb = getOne(id);
		if (inDb.isEmpty()) {
			return create(entity);
		} else {
			QuoteSourceType inDbEntity = inDb.get();
			BeanUtils.copyProperties(entity, inDbEntity, "id");
			repo.save(inDbEntity);
			return inDbEntity;
		}
	}

	@Override
	public void delete(QuoteSourceType entity) {
		deleteById(entity.getId());
	}

	@Override
	public void deleteById(Integer id) {
		repo.deleteById(id);
	}
}
