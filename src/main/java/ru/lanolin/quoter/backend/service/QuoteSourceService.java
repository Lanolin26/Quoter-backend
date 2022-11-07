package ru.lanolin.quoter.backend.service;

import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.repo.QuoteSourceRepository;
import ru.lanolin.quoter.backend.util.RestApi;

@Service
public class QuoteSourceService implements RestApi<QuoteSource, Integer> {

	private final QuoteSourceRepository repo;

	public QuoteSourceService(QuoteSourceRepository repo) {
		this.repo = repo;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public QuoteSourceRepository getRepo() {
		return repo;
	}

	@Override
	public void copyProperties(QuoteSource entity, QuoteSource inDbEntity) {
		inDbEntity.setSourceName(entity.getSourceName());
		inDbEntity.getType().setId(entity.getType().getId());
	}

	@Override
	public void checkCorrect(QuoteSource entity) throws IncorrectField {
		entity.setId(null);
	}
}
