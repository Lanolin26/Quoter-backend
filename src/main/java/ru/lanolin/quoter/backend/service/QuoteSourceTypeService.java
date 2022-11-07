package ru.lanolin.quoter.backend.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.exceptions.domain.IncorrectField;
import ru.lanolin.quoter.backend.repo.QuoteSourceTypeRepository;
import ru.lanolin.quoter.backend.util.RestApi;

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
	public void copyProperties(QuoteSourceType entity, QuoteSourceType inDbEntity) {
		BeanUtils.copyProperties(entity, inDbEntity, "id");
	}

	@Override
	public void checkCorrect(QuoteSourceType entity) throws IncorrectField {
		entity.setId(null);
	}
}
