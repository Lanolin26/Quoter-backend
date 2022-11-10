package ru.lanolin.quoter.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.exceptions.domain.CheckArgs;
import ru.lanolin.quoter.backend.repo.QuoteSourceTypeRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Objects;

@Service
public class QuoteSourceTypeService implements RestApi<QuoteSourceType, Integer> {

	private final List<CheckArgs<QuoteSourceType>> ARGS_LIST = List.of(
			new CheckArgs<>(Objects::isNull, "class", "null", "Entity mustn't be a null"),
			new CheckArgs<>(e -> Objects.isNull(e.getType()) || e.getType().isEmpty() || e.getType().isBlank(),
					"type", "null", "Not valid null value")
	);
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
		inDbEntity.setType(entity.getType());
	}

	@Override
	public List<CheckArgs<QuoteSourceType>> checks() {
		return ARGS_LIST;
	}

}
