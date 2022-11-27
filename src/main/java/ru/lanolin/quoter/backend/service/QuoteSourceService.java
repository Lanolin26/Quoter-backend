package ru.lanolin.quoter.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.exceptions.domain.CheckArgs;
import ru.lanolin.quoter.backend.repo.QuoteSourceRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Service
public class QuoteSourceService implements RestApi<QuoteSource, Integer> {

	private final List<CheckArgs<QuoteSource>> ARGS_LIST = List.of(
			new CheckArgs<>(Objects::isNull,
					"class", "null", "Entity mustn't be a null"),
			new CheckArgs<>(e -> Objects.isNull(e.getSourceName()) || e.getSourceName().isEmpty() || e.getSourceName().isBlank(),
					"Source_Name", "empty", "Not available empty value"),
			new CheckArgs<>(e -> Objects.isNull(e.getType()) || Objects.isNull(e.getType().getId()),
					"type", "empty", "Type is empty"),
			new CheckArgs<>(Predicate.not(this::existType),
					"type", "not_found", "Valid type not found")
	);

	private final QuoteSourceRepository repo;
	private final QuoteSourceTypeService sourceTypeService;

	@Autowired
	public QuoteSourceService(QuoteSourceRepository repo, QuoteSourceTypeService sourceTypeService) {
		this.repo = repo;
		this.sourceTypeService = sourceTypeService;
	}

	private boolean existType(QuoteSource e) {
		return this.sourceTypeService.exist(e.getType());
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public QuoteSourceRepository getRepo() {
		return repo;
	}

	@Override
	public void copyProperties(QuoteSource entity, QuoteSource inDbEntity) {
		inDbEntity.setSourceName(entity.getSourceName());
		inDbEntity.setType(new QuoteSourceType(entity.getType().getId()));
	}

	@Override
	public List<CheckArgs<QuoteSource>> checks() {
		return ARGS_LIST;
	}
}
