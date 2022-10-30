package ru.lanolin.quoter.backend.service;

import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.repo.QuoteSourceRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Optional;

@Service
public class QuoteSourceService implements RestApi<QuoteSource, Integer> {

	private final QuoteSourceRepository repo;

	public QuoteSourceService(QuoteSourceRepository repo) {
		this.repo = repo;
	}

	@Override
	public List<QuoteSource> findAll() {
		return repo.findAll();
	}

	@Override
	public Optional<QuoteSource> getOne(Integer id) {
		return repo.findById(id);
	}

	@Override
	public QuoteSource create(QuoteSource entity) {
		repo.save(entity);
		return entity;
	}

	@Override
	public QuoteSource update(Integer id, QuoteSource entity) {
		Optional<QuoteSource> inDb = getOne(id);
		if (inDb.isEmpty()) {
			return create(entity);
		} else {
			QuoteSource inDbEntity = inDb.get();
			inDbEntity.setSourceName(entity.getSourceName());
			inDbEntity.getType().setId(entity.getType().getId());
			//			BeanUtils.copyProperties(entity, inDbEntity, "id");
			repo.save(inDbEntity);
			return inDbEntity;
		}
	}

	@Override
	public void delete(QuoteSource entity) {
		deleteById(entity.getId());
	}

	@Override
	public void deleteById(Integer id) {
		repo.deleteById(id);
	}
}
