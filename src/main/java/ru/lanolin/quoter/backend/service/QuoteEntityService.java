package ru.lanolin.quoter.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.UserEntity;
import ru.lanolin.quoter.backend.exceptions.domain.CheckArgs;
import ru.lanolin.quoter.backend.repo.QuoteEntityRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class QuoteEntityService implements RestApi<QuoteEntity, Integer> {

	private final List<CheckArgs<QuoteEntity>> checkArgs = List.of(
			new CheckArgs<>(Objects::isNull,
					"class", "null", "Entity mustn't be a null"),
			new CheckArgs<>(e -> Objects.isNull(e.getText()) || e.getText().isEmpty() || e.getText().isBlank(),
					"text", "empty", "Not available empty value"),
			new CheckArgs<>(e -> Objects.isNull(e.getSource()) || Objects.isNull(e.getSource().getId()),
					"source", "empty", "Not available empty value"),
			new CheckArgs<>(e -> Objects.isNull(e.getAuthor()) || Objects.isNull(e.getAuthor().getId()),
					"author", "empty", "Not available empty value"),
			new CheckArgs<>(Predicate.not(this::existSource),
					"source", "not_found", "Not found valid entity"),
			new CheckArgs<>(Predicate.not(this::existAuthor),
					"author", "not_found", "Not found valid entity")
	);

	private final QuoteEntityRepository repo;
	private final QuoteSourceService quoteSourceService;
	private final UserEntityService userService;

	@Autowired
	public QuoteEntityService(QuoteEntityRepository repo,
	                          QuoteSourceService quoteSourceService,
	                          UserEntityService userService) {
		this.repo = repo;
		this.quoteSourceService = quoteSourceService;
		this.userService = userService;
	}

	private boolean existSource(QuoteEntity e) {
		return this.quoteSourceService.exist(e.getSource());
	}

	private boolean existAuthor(QuoteEntity e) {
		return this.userService.exist(e.getAuthor());
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public QuoteEntityRepository getRepo() {
		return repo;
	}

	@Override
	public void copyProperties(QuoteEntity entity, QuoteEntity inDbEntity) {
		inDbEntity.setSource(new QuoteSource(entity.getSource().getId()));
		inDbEntity.setAuthor(new UserEntity(entity.getAuthor().getId()));
		inDbEntity.setText(
				Optional.ofNullable(entity.getText())
						.map(text -> text.replaceAll("&" + "nbsp;", " "))
						.map(text -> text.replaceAll(String.valueOf((char) 160), " "))
						.orElse(null));
	}

	@Override
	public List<CheckArgs<QuoteEntity>> checks() {
		return checkArgs;
	}
}
